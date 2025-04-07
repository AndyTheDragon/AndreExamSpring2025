package dat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.dao.TripDAO;
import dat.dto.*;
import dat.enums.TripCategory;
import dat.exceptions.ApiException;
import dat.exceptions.DaoException;
import dat.utils.DataAPIReader;
import dat.utils.Populator;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TripController
{
    private final EntityManagerFactory emf;
    private final TripDAO dao;
    private final Logger logger = LoggerFactory.getLogger(TripController.class);

    public TripController(EntityManagerFactory emf)
    {
        this.emf = emf;
        this.dao = new TripDAO(emf);
    }

    public void getAllTrips(Context ctx)
    {
        try
        {
            List<TripDTO> trips = dao.getAll();
            ctx.json(trips);
        } catch (DaoException e)
        {
            throw new ApiException(404, "Trips not found");
        }
    }

    public void getTripById(Context ctx)
    {
        try
        {
            Integer id = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            logger.info("Trip ID: " + id);
            Boolean withItems = ctx.queryParamAsClass("withItems", Boolean.class)
                    .check(p -> p != null, "withItems is missing")
                    .getOrDefault(false);
            logger.info("withItems: " + withItems);
            TripDTO trip = dao.getById(id);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            ObjectNode tripJson = mapper.valueToTree(trip);
            if (withItems)
            {
                List<ItemDTO> items = fetchPackingItems(trip.getCategory());
                ArrayNode itemsArray = mapper.valueToTree(items);
                tripJson.set("items", itemsArray);
            }
            ctx.json(tripJson);
        } catch (IllegalArgumentException e)
        {
            logger.error("Illegal argument. ", e);
            throw new ApiException(400, "Invalid ID format", e);
        } catch (DaoException e)
        {
            throw new ApiException(404, "Trip not found", e);
        }
    }

    public void createTrip(Context ctx)
    {
        try
        {
            TripInputDTO tripInput = ctx.bodyAsClass(TripInputDTO.class);
            dao.create(tripInput);
            ctx.status(201);
        } catch (IllegalArgumentException | DaoException e)
        {
            throw new ApiException(400, "Invalid input data", e);
        }
    }

    public void updateTrip(Context ctx)
    {
        try
        {
            TripInputDTO tripInput = ctx.bodyAsClass(TripInputDTO.class);
            Integer id = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            TripDTO updatedTrip = dao.update(tripInput, id);
            ctx.json(updatedTrip);
        } catch (IllegalArgumentException | DaoException e)
        {
            throw new ApiException(400, "Invalid input data", e);
        }
    }

    public void deleteTrip(Context ctx)
    {
        try
        {
            Integer id = ctx.pathParamAsClass("id", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("ID must be a positive integer"));
            dao.deleteTrip(id);
            ctx.status(204);
        } catch (IllegalArgumentException | DaoException e)
        {
            throw new ApiException(400, "Invalid ID format", e);
        }
    }

    public void addGuideToTrip(Context ctx)
    {
        try
        {
            Integer tripId = ctx.pathParamAsClass("tripId", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("Trip ID must be a positive integer"));
            Integer guideId = ctx.pathParamAsClass("guideId", Integer.class)
                    .check(i -> i > 0, "ID must be a positive integer")
                    .getOrThrow((validator) -> new IllegalArgumentException("Guide ID must be a positive integer"));
            dao.addGuideToTrip(tripId, guideId);
            ctx.status(204);
        } catch (IllegalArgumentException | DaoException e)
        {
            throw new ApiException(400, "Invalid input data", e);
        }
    }

    public void populate(Context ctx)
    {
        Populator populator = new Populator();
        populator.populate(emf);
        ctx.status(204);
    }

    public void getByCategory(Context ctx)
    {
        try
        {
            String category = ctx.queryParamAsClass("category", String.class)
                    .check(this::isValidTripCategory, "Invalid category")
                    .getOrThrow((validator) -> new IllegalArgumentException("Category is missing or invalid"));
            List<TripDTO> trips = dao.getByCategory(category);
            ctx.json(trips);
        } catch (DaoException e)
        {
            throw new ApiException(404, "No trips found");
        }
    }

    public void getGuidesTotalPrice(Context ctx)
    {
        try
        {
            List<TotalPriceDTO> totalPriceList = dao.getGuidesTotalPrice();
            ctx.json(totalPriceList);
        }
        catch (DaoException e)
        {
            throw new ApiException(404, "No total price found");
        }
    }

    public List<ItemDTO> fetchPackingItems(TripCategory tripCategory)
    {
        try
        {
            DataAPIReader dataAPIReader = new DataAPIReader();
            String url = "https://packingapi.cphbusinessapps.dk/packinglist/" + tripCategory.toString().toLowerCase();
            String jsonResponse = dataAPIReader.getDataFromClient(url);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ItemsResponseDTO itemsResponse = objectMapper.readValue(jsonResponse, ItemsResponseDTO.class);
            return itemsResponse.getItems();
        } catch (JsonMappingException e)
        {
            throw new ApiException(500, "Error mapping JSON response to DTO", e);
        } catch (JsonProcessingException e)
        {
            throw new ApiException(500, "Error parsing JSON", e);
        }
    }

    private Long calcWeight(List<ItemDTO> items)
    {
        return items.stream()
                .mapToLong(ItemDTO::getWeightInGrams)
                .sum();
    }

    private boolean isValidTripCategory(String category)
    {
        try
        {
            TripCategory.valueOf(category.toUpperCase());
            return true;
        } catch (IllegalArgumentException e)
        {
            return false;
        }
    }
}
