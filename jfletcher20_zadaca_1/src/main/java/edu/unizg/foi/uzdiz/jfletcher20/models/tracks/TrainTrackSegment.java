package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITrainTrackSegmentState;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;

public class TrainTrackSegment {
    public TrainTrack mainTrack;
    public Station startStation;
    public Station endStation;
    public ITrainTrackSegmentState state;

    public TrainTrackSegment(
            TrainTrack mainTrack,
            Station startStation,
            Station endStation,
            ITrainTrackSegmentState state //
    ) {
        this.mainTrack = mainTrack;
        this.startStation = startStation;
        this.endStation = endStation;
        this.state = state;
    }

    public boolean isBidirectional() {
        return this.mainTrack.trackCount() == 1;
    }

    public boolean hasStation(Station station) {
        return this.startStation.equals(station) || this.endStation.equals(station);
    }

    public ITrainTrackSegmentState getState() {
        return this.state;
    }

    public boolean setState(ITrainTrackSegmentState state) {
        return state.setState(this, state);
    }

    public boolean isFunctional() {
        return this.state.internalState() == TrainTrackStatus.FUNCTIONAL;
    }

    public boolean isClosed() {
        return this.state.internalState() == TrainTrackStatus.CLOSED;
    }

    public boolean isFaulty() {
        return this.state.internalState() == TrainTrackStatus.FAULTY;
    }

    public boolean isTesting() {
        return this.state.internalState() == TrainTrackStatus.TESTING;
    }

    public TrainTrackStatus getStatus() {
        return this.state.internalState();
    }
}
