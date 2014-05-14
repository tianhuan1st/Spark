package cn.bmwm.navigate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple class to test translation functionality.
 */
public class TestNavigator {

    public static void main(String[] args) {

        boolean again = true;
        while (again) {
            System.out.println("起点 Please enter origin");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String origin = "";
            try {
                origin = reader.readLine();
            } catch (IOException e) {
                System.out.println("Could not read origin:" + e);
            }

            if (origin == null) {
                System.out.println("Sorry, I didnt get origin.");
                continue;
            }

            System.out.println("目的地 Please enter destination");
             reader = new BufferedReader(new InputStreamReader(System.in));
            String destination = "";
            try {
                destination = reader.readLine();
            } catch (IOException e) {
                System.out.println("Could not read destination:" + e);
            }

            if (destination == null) {
                System.out.println("Sorry, I didnt get destination.");
                continue;
            }

            System.out.println("起点所在城市 Please enter origin_region");
            reader = new BufferedReader(new InputStreamReader(System.in));
            String origin_region = "";
            try {
                origin_region = reader.readLine();
            } catch (IOException e) {
                System.out.println("Could not read origin_region:" + e);
            }

            if (origin_region == null) {
                System.out.println("Sorry, I didnt get origin_region.");
                continue;
            }


            System.out.println("目的地 所在城市 Please enter destination_region");
            reader = new BufferedReader(new InputStreamReader(System.in));
            String destination_region = "";
            try {
                destination_region = reader.readLine();
            } catch (IOException e) {
                System.out.println("Could not read destination_region:" + e);
            }

            if (destination_region == null) {
                System.out.println("Sorry, I didnt get destination_region.");
                continue;
            }

            String result = DirectionUtil.navigate(origin,destination,origin_region,destination_region);

            System.out.println("起点 origin:" + origin);
            System.out.println("目的地 destination: " + destination);
            System.out.println("起点所在城市 origin_region: " + origin_region);
            System.out.println("目标所在城市 destination_region: " + destination_region);


            System.out.println("导航路径 The result is:\n" + result);

            System.out.println("Do you want to continue testing? y/n");
            String cont;
            try {
                cont = reader.readLine();
                if ("yes".equals(cont.toLowerCase().trim()) || "y".equals(cont.toLowerCase().trim())) {
                    again = true;
                } else {
                    again = false;
                }
            } catch (IOException e) {
                System.out.println("Could not read text:" + e);
            }
        }
    }

}
