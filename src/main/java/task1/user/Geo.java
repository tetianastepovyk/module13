package task1.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Geo {
    private double lat;
    private double lng;

    public Geo(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "GEO{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
