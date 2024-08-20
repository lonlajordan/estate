package com.estate.domain.service.face;

public interface ParticipationService {
    int countMedalOfPlayer(long id);

    Long averageAgeOfPlayerByGameId(long id);

    long countPlayerByGameIdAndGender(long id, char m);
}
