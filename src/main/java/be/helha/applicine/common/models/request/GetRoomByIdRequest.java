package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Room;

public class GetRoomByIdRequest extends ClientEvent {
    private int roomId;
    private Room room;

    public GetRoomByIdRequest(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) {
        requestVisitor.visit(this);
    }
}
