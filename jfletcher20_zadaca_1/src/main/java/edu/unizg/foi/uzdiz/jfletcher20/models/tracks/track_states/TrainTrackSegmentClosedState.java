package edu.unizg.foi.uzdiz.jfletcher20.models.tracks.track_states;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackSegment;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITrainTrackSegmentState;

public class TrainTrackSegmentClosedState implements ITrainTrackSegmentState {
    @Override
    public TrainTrackStatus internalState() {
        return TrainTrackStatus.CLOSED;
    }

    @Override
    public boolean setState(TrainTrackSegment segment, ITrainTrackSegmentState state, TraversalDirection direction) {
        if (state.internalState() == internalState())
            return false;
        // CLOSED can only change into TESTING
        if (state.internalState() == TrainTrackStatus.TESTING) {
            if (direction == TraversalDirection.FORTH)
                segment.stateForth = state;
            else
                segment.stateReverse = state;
            return true;
        }
        return false;
    }
}
