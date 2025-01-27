package edu.unizg.foi.uzdiz.jfletcher20.models.tracks.track_states;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackSegment;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITrainTrackSegmentState;

public class TrainTrackSegmentTestingState implements ITrainTrackSegmentState {
    @Override
    public TrainTrackStatus internalState() {
        return TrainTrackStatus.TESTING;
    }

    @Override
    public boolean setState(TrainTrackSegment segment, ITrainTrackSegmentState state, TraversalDirection direction) {
        if (state.internalState() == internalState())
            return false;
        // TESTING can only change into FUNCTIONAL or FAULTY
        if (state.internalState() == TrainTrackStatus.CLOSED)
            return false;
        if (direction == TraversalDirection.FORTH)
            segment.stateForth = state;
        else
            segment.stateReverse = state;
        return true;
    }
}
