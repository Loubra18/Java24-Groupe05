package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Room;

import java.util.List;

public class GetRoomsRequest extends ClientEvent {
    private List<Room> rooms;
    @Override
    public void dispatchOn(RequestVisitor requestVisitor) {
        requestVisitor.visit(this);
    }

    public void setRooms(List<Room> all) {
        this.rooms = all;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
