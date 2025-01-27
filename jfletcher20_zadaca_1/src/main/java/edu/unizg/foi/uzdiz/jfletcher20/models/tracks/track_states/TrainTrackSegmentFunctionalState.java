package edu.unizg.foi.uzdiz.jfletcher20.models.tracks.track_states;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackSegment;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITrainTrackSegmentState;

public class TrainTrackSegmentFunctionalState implements ITrainTrackSegmentState {
    @Override
    public TrainTrackStatus internalState() {
        return TrainTrackStatus.FUNCTIONAL;
    }

    @Override
    public boolean setState(TrainTrackSegment segment, ITrainTrackSegmentState state, TraversalDirection direction) {
        if (state.internalState() == internalState())
            return false;
        if (direction == TraversalDirection.FORTH)
            segment.stateForth = state;
        else
            segment.stateReverse = state;
        return true;
    }
}
