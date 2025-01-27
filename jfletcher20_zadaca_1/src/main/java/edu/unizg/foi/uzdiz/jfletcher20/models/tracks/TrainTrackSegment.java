package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITrainTrackSegmentState;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class TrainTrackSegment {
    public TrainTrack mainTrack;
    public Station startStation;
    public Station endStation;
    public ITrainTrackSegmentState stateForth, stateReverse;

    public TrainTrackSegment(
            TrainTrack mainTrack,
            Station startStation,
            Station endStation,
            ITrainTrackSegmentState state //
    ) {
        this.mainTrack = mainTrack;
        this.startStation = startStation;
        this.endStation = endStation;
        this.stateForth = state;
        this.stateReverse = state;
    }

    public boolean isBidirectional() {
        return this.mainTrack.trackCount() == 1;
    }

    public boolean hasStation(Station station) {
        return this.startStation.equals(station) || this.endStation.equals(station);
    }

    public ITrainTrackSegmentState getStateForth() {
        return this.stateForth;
    }

    public boolean setStateForth(ITrainTrackSegmentState state) {
        if (isBidirectional())
            return this.stateForth.setState(this, state, TraversalDirection.FORTH)
                    && this.stateReverse.setState(this, state, TraversalDirection.REVERSE);
        else
            return this.stateForth.setState(this, state, TraversalDirection.FORTH);
    }

    public TrainTrackStatus getStatusForth() {
        return this.stateForth.internalState();
    }

    public boolean isFunctionalForth() {
        return this.stateForth.internalState() == TrainTrackStatus.FUNCTIONAL;
    }

    public boolean isClosedForth() {
        return this.stateForth.internalState() == TrainTrackStatus.CLOSED;
    }

    public boolean isFaultyForth() {
        return this.stateForth.internalState() == TrainTrackStatus.FAULTY;
    }

    public boolean isTestingForth() {
        return this.stateForth.internalState() == TrainTrackStatus.TESTING;
    }

    public TrainTrackStatus getStatusReverse() {
        return this.stateReverse.internalState();
    }

    public boolean setStateReverse(ITrainTrackSegmentState state) {
        if (isBidirectional())
            return this.stateReverse.setState(this, state, TraversalDirection.REVERSE)
                    && this.stateForth.setState(this, state, TraversalDirection.FORTH);
        else
            return this.stateReverse.setState(this, state, TraversalDirection.REVERSE);
    }

    public boolean isFunctionalReverse() {
        return this.stateReverse.internalState() == TrainTrackStatus.FUNCTIONAL;
    }

    public boolean isClosedReverse() {
        return this.stateReverse.internalState() == TrainTrackStatus.CLOSED;
    }

    public boolean isFaultyReverse() {
        return this.stateReverse.internalState() == TrainTrackStatus.FAULTY;
    }

    public boolean isTestingReverse() {
        return this.stateReverse.internalState() == TrainTrackStatus.TESTING;
    }

    public TrainTrackStatus getStatus(TraversalDirection direction) {
        return direction.equals(TraversalDirection.FORTH) ? this.getStatusForth() : this.getStatusReverse();
    }

    public List<TrainTrackStatus> getStatuses() {
        if (!isBidirectional())
            return List.of(this.getStatusForth(), this.getStatusReverse());
        else
            return List.of(this.getStatusForth());
    }

    public List<String> getStatusesDisplay() {
        if (!isBidirectional())
            return List.of(
                    "Smjer N: " + this.getStatusForth(), "Smjer O: " + this.getStatusReverse());
        else
            return List.of("Obosmjerno: " + this.getStatusForth());
    }

    public void setState(ITrainTrackSegmentState state, TraversalDirection traversalDirection) {
        if (traversalDirection == TraversalDirection.FORTH) {
            if (!setStateForth(state))
                Logs.e("Nije moguće postaviti status relacije " + this.mainTrack.id()
                        + startStation.name() + " - " + endStation.name() + " u smjeru "
                        + traversalDirection + " sa statusa " + this.getStatusForth()
                        + " na status " + state.internalState());
        } else if (!setStateReverse(state)) {
            Logs.e("Nije moguće postaviti status relacije " + this.mainTrack.id()
                    + startStation.name() + " - " + endStation.name() + " u smjeru "
                    + traversalDirection + " sa statusa " + this.getStatusReverse()
                    + " na status " + state.internalState());
        }
    }
}
