package edu.unizg.foi.uzdiz.jfletcher20.models.tracks.track_states;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackSegment;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITrainTrackSegmentState;

public class TrainTrackSegmentFunctionalState implements ITrainTrackSegmentState {
    @Override
    public TrainTrackStatus internalState() {
        return TrainTrackStatus.FUNCTIONAL;
    }
    @Override
    public boolean setState(TrainTrackSegment segment, ITrainTrackSegmentState state) {
        segment.state = state;
        return true;
    }
}
