
/**
 * Problem 8: Parking Lot Management with Open Addressing
 * Array-based hash table with linear probing.
 */
public class P8_ParkingLotOpenAddressing {

    private enum Status { EMPTY, OCCUPIED, DELETED }

    private static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status = Status.EMPTY;
    }

    private final ParkingSpot[] spots;
    private final int capacity;
    private int occupantCount = 0;

    public P8_ParkingLotOpenAddressing(int capacity) {
        this.capacity = capacity;
        this.spots = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) spots[i] = new ParkingSpot();
    }

    public void parkVehicle(String licensePlate) {
        if (occupantCount >= capacity * 0.75) {
            System.out.println("Wait! Load factor high. Parking lot almost full.");
        }

        int preferredSpot = Math.abs(licensePlate.hashCode() % capacity);
        int currentSpot = preferredSpot;
        int probes = 0;

        while (spots[currentSpot].status == Status.OCCUPIED) {
            currentSpot = (currentSpot + 1) % capacity;
            probes++;
            if (probes >= capacity) {
                System.out.println("Parking lot full!");
                return;
            }
        }

        // Park the car
        spots[currentSpot].licensePlate = licensePlate;
        spots[currentSpot].entryTime = System.currentTimeMillis();
        spots[currentSpot].status = Status.OCCUPIED;
        occupantCount++;

        System.out.printf("Parked %s at spot #%d (%d probes)%n", licensePlate, currentSpot, probes);
    }

    public void exitVehicle(String licensePlate) {
        int preferredSpot = Math.abs(licensePlate.hashCode() % capacity);
        int currentSpot = preferredSpot;
        int probes = 0;

        while (spots[currentSpot].status != Status.EMPTY) {
            if (spots[currentSpot].status == Status.OCCUPIED && 
                spots[currentSpot].licensePlate.equals(licensePlate)) {
                
                // Calculate fee (Dummy $10/hr)
                long durationSeconds = (System.currentTimeMillis() - spots[currentSpot].entryTime) / 1000;
                double fee = Math.max(5.0, durationSeconds * 0.1); // Min $5

                spots[currentSpot].status = Status.DELETED;
                occupantCount--;
                System.out.printf("Exit %s: Duration %ds, Fee $%.2f%n", licensePlate, durationSeconds, fee);
                return;
            }
            currentSpot = (currentSpot + 1) % capacity;
            probes++;
            if (probes >= capacity) break;
        }
        System.out.println("Vehicle not found.");
    }

    public static void main(String[] args) throws InterruptedException {
        P8_ParkingLotOpenAddressing lot = new P8_ParkingLotOpenAddressing(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235"); // Likely collision
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(1000);
        lot.exitVehicle("ABC-1234");
    }
}
