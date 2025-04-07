package dat.dao;

import dat.dto.TripDTO;

import java.util.Set;

public interface ITripGuideDAO
{
    void addGuideToTrip(int tripId, int guideId);
    Set<TripDTO> getTripsByGuide(int guideId);
}
