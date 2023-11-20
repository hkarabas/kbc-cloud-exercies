import model.CarStatics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final int DISTANCE = 10;

    public static void main(String[] args) {

        List<CarStatics> carStaticsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("measurements.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                String plateNumber = values[0];
                Double entryTime = Double.valueOf(values[1])*3600 + Double.valueOf(values[2])*60+Double.valueOf(values[3])+(Double.valueOf(values[4])/1000)%60;
                Double exitTime = Double.valueOf(values[5])*3600 + Double.valueOf(values[6])*60+Double.valueOf(values[7])+(Double.valueOf(values[8])/1000)%60;
                Double speed = DISTANCE/((exitTime-entryTime)/3600);
                CarStatics carStatics = new CarStatics(plateNumber,entryTime,exitTime,speed);
                carStaticsList.add(carStatics);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(String.format("The data of %d vehicles were recorded in the measurement",carStaticsList.size()));
        long passedBeforeNine = carStaticsList.stream().filter(t->t.exitTime()/3600<9).count();
        System.out.println(String.format("Before 9 o'clock %d vehicles passed the exit point recorder.",passedBeforeNine));
        Scanner in = new Scanner(System.in);
        System.out.println("You entered hour :");
        int hour = in.nextInt();
        System.out.println("You entered minutes : ");
        int minutes = in.nextInt();

        long passtimeseconds = hour*3600+minutes*60;
        long passedcars = carStaticsList.stream().filter(carStatics -> carStatics.entryTime()<passtimeseconds && carStatics.exitTime()>passtimeseconds).count();
        System.out.println(String.format("The number of vehicle that passed the entry point recorder: %d",passedcars));

        long passtimeseconds2 = passtimeseconds+60;
        float traffic_intensity =  carStaticsList.stream().filter(carStatics -> carStatics.entryTime()<passtimeseconds && carStatics.exitTime()<passtimeseconds2).count()/DISTANCE;
        System.out.println(String.format("The traffic intensity: %f",traffic_intensity));

        CarStatics carStatics = carStaticsList.stream().max((o1, o2) -> o1.speed().compareTo(o2.speed())).get();
        System.out.println(carStatics);
        System.out.println(String.format("The data of the vehicle with the highest speed are \n" +
                String.format("license plate number: %s \n",carStatics.plateNumber()) +
                String.format("average speed: %d km/h",carStatics.speed().intValue())));
       double average = carStaticsList.stream().mapToDouble(CarStatics::speed).average().orElse(0.0d);
       long countCars = carStaticsList.stream().filter(carStatics1 -> carStatics1.speed()<average).count();
       System.out.println(String.format("number of overtaken vehicles: %d",countCars));
       long speedOver = carStaticsList.stream().filter(carStatics1 -> carStatics1.speed() > 90).count();

       double percatange = speedOver*100/carStaticsList.size();
       System.out.println(String.format("%f percent of the vehicles were speeding.",percatange));

    }
}""