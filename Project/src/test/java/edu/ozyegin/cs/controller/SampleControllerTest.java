package edu.ozyegin.cs.controller;

import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import edu.ozyegin.cs.entity.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

public class SampleControllerTest extends IntegrationTestSuite {
    private List<Sample> generateSamples(int size) {
        ArrayList<Sample> samples = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            samples.add(new Sample()
                    .name(RandomStringUtils.random(random(10), true, true))
                    .data(RandomStringUtils.random(random(10), true, true))
                    .value(random(100)));
        }
        return samples;



    }

    @Test
    public void helloWorld() throws Exception {
        HashMap response = getMethod("/sample", HashMap.class);
        System.out.println(response);
        //Assert.assertEquals("Hello World", response.get("message"));
    }

    @Test
    public void echo() throws Exception {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("message", "I am doing the CS202 Project!");

        HashMap response = postMethod("/sample/echo", HashMap.class, payload);
        Assert.assertEquals(payload.get("message"), response.get("echo"));
    }

    @Test
    public void create1() throws Exception {
        List<Sample> samples = generateSamples(1);
        System.out.println(samples.get(0).getName());

        postMethod("/sample/create", String.class, samples);

        List<Sample> data = Objects.requireNonNull(jdbcTemplate)
                .query("SELECT * FROM Sample", new BeanPropertyRowMapper<>(Sample.class));

        assertTwoListEqual(samples, data);
        System.out.println(data.get(0).getName());
    }

    @Test
    public void create42() throws Exception {
        List<Sample> samples = generateSamples(42);

        postMethod("/sample/create", String.class, samples);

        List<Sample> data = Objects.requireNonNull(jdbcTemplate)
                .query("SELECT * FROM Sample", new BeanPropertyRowMapper<>(Sample.class));

        assertTwoListEqual(samples, data);
    }

    @Test
    public void create3() throws Exception {
        List<Sample> samples = generateSamples(3);

        postMethod("/sample/create", String.class, samples);

        List<Sample> data = Objects.requireNonNull(jdbcTemplate)
                .query("SELECT * FROM Sample", new BeanPropertyRowMapper<>(Sample.class));
        System.out.println(data);

        assertTwoListEqual(samples, data);
    }

    @Test
    public void fetch3() throws Exception {
        List<Sample> samples = generateSamples(3);
        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Sample (name, data, value) VALUES(?,?,?)",
                        samples,
                        10,
                        (ps, sample) -> {
                            ps.setString(1, sample.getName());
                            ps.setString(2, sample.getData());
                            ps.setInt(3, sample.getValue());
                        }
                );

        Gson gson = new Gson(); // for object type conversion

        HashMap response = getMethod("/sample/entities", HashMap.class);
        System.out.println(response);
        for (int i = 0; i < samples.size(); i++) {
            HashMap<String, Object> aObj = ((List<HashMap>) response.get("samples")).get(i);
//            System.out.println(response);

            // properly serialize HashMap object to Sample object
            Sample a = gson.fromJson(gson.toJsonTree(aObj), Sample.class);
            // get data reference
            Sample b = samples.get(i);
//            System.out.println(b);

            assert a.equals(b);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////STOP TEST FUNCTION START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void createStop() throws Exception {
        ArrayList<Stops> stopsarr = new ArrayList<>();
        stopsarr.add(new Stops().name("stop1"));
        List<Stops> stops = stopsarr;
        postMethod("/stop/create", String.class, stops);
    }

    @Test
    public void updateStop() throws Exception {
        int stopid = 2;
        String stopname = "stop46";
        HashMap<String, Object> payload = new HashMap<String,Object>();
        payload.put("StopId", stopid);
        payload.put("NewName", stopname);
        HashMap response = postMethod("/stop/modify/rename", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
    }

    @Test
    public void deleteStop() throws Exception {
        ArrayList<Stops> stopsarr = new ArrayList<>();
        stopsarr.add(new Stops().id(4));
        List<Stops> stops = stopsarr;
        postMethod("/stop/modify/delete", String.class, stops);
    }

    @Test
    public void getStops() throws Exception {
        ArrayList<Stops> stopsarr = new ArrayList<>();
        stopsarr.add(new Stops().name("stop1"));
        stopsarr.add(new Stops().name("stop2"));
        stopsarr.add(new Stops().name("stop3"));
        List<Stops> stops = stopsarr;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Stops (name) VALUES(?)",
                        stops,
                        10,
                        (ps, stopp) -> {
                            ps.setString(1, stopp.getName());
                        }
                );
        HashMap response = getMethod("/stop/get_all", HashMap.class);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("stops"));
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////STOP TEST FUNCTION END//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////Train TEST FUNCTION START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void createTrain() throws Exception {
        ArrayList<Trains> trainsarr = new ArrayList<>();
        trainsarr.add(new Trains().name("train1"));
        List<Trains> trains = trainsarr;
        postMethod("/train/create", String.class, trains);
        List<Trains> data = Objects.requireNonNull(jdbcTemplate)
                .query("SELECT * FROM Trains", new BeanPropertyRowMapper<>(Trains.class));
//        System.out.println(data.get(0).getId());
//        System.out.println(data.get(0).getName());
    }

    @Test
    public void updateTrain() throws Exception {
        int trainid = 2;
        String trainname = "stop46";
        HashMap<String, Object> payload = new HashMap<String,Object>();
        payload.put("TrainId", trainid);
        payload.put("NewName", trainname);
        HashMap response = postMethod("/train/modify/rename", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
    }

    @Test
    public void deleteTrain() throws Exception {
        ArrayList<Trains> trainsarr = new ArrayList<>();
        trainsarr.add(new Trains().id(4));
        List<Trains> trains = trainsarr;
        postMethod("/train/modify/delete", String.class, trains);
    }

    @Test
    public void getTrains() throws Exception {
        ArrayList<Trains> trainsarr = new ArrayList<>();
        trainsarr.add(new Trains().name("train1"));
        trainsarr.add(new Trains().name("train2"));
        trainsarr.add(new Trains().name("train3"));
        List<Trains> trains = trainsarr;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Trains (name) VALUES(?)",
                        trains,
                        10,
                        (ps, trainn) -> {
                            ps.setString(1, trainn.getName());
                        }
                );
        HashMap response = getMethod("/train/get_all", HashMap.class);
        System.out.println("success: " + response.get("success"));
//        System.out.println(response.get("trains"));
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////Train TEST FUNCTION END//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////Route TEST FUNCTION START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void createRoute() throws Exception {
        String routename = "route46";
        HashMap<String, Object> payload = new HashMap<String,Object>();
        payload.put("RouteName", routename);
        int[] rstops = {1};
        payload.put("stop_ids", rstops);
        HashMap response = postMethod("/route/create", HashMap.class, payload);
    }

    @Test
    public void updateRoute() throws Exception {
        int routeid = 2;
        String routename = "route46";
        HashMap<String, Object> payload = new HashMap<String,Object>();
        payload.put("RouteId", routeid);
        payload.put("NewName", routename);
        HashMap response = postMethod("/route/modify/rename", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
    }

    @Test
    public void addStop() throws Exception {
        int routeid = 2;
        int stopid = 1;
        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("RouteId", routeid);
        payload.put("StopId", stopid);
        HashMap response = postMethod("/route/modify/add_stop", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
//        System.out.println(response.get("message"));
    }

    @Test
    public void removeStop() throws Exception {
        int routeid = 2;
        int stopid = 1;
        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("RouteId", routeid);
        payload.put("StopId", stopid);
        HashMap response = postMethod("/route/modify/remove_stop", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
//        System.out.println(response.get("message"));
    }

    @Test
    public void deleteRoute() throws Exception {
//        ArrayList<Routes> routesarr = new ArrayList<>();
//        routesarr.add(new Routes().name("route1"));
//        routesarr.add(new Routes().name("route2"));
//        routesarr.add(new Routes().name("route3"));
//        List<Routes> routes = routesarr;
//
//        Objects.requireNonNull(jdbcTemplate)
//                .batchUpdate(
//                        "INSERT INTO Routes (name) VALUES(?)",
//                        routes,
//                        10,
//                        (ps, routee) -> {
//                            ps.setString(1, routee.getName());
//                        }
//                );
        int routeid = 2;
        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("RouteId", routeid);
        HashMap response = postMethod("/route/delete", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
//        System.out.println(response.get("routes"));
    }

    @Test
    public void getRoute() throws Exception {
        ArrayList<Routes> routesarr = new ArrayList<>();
        routesarr.add(new Routes().name("route1"));
        routesarr.add(new Routes().name("route2"));
        routesarr.add(new Routes().name("route3"));
        List<Routes> routes = routesarr;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Routes (name) VALUES(?)",
                        routes,
                        10,
                        (ps, routee) -> {
                            ps.setString(1, routee.getName());
                        }
                );
        ArrayList<Stops> stopsarr = new ArrayList<>();
        stopsarr.add(new Stops().name("stop1"));
        stopsarr.add(new Stops().name("stop2"));
        stopsarr.add(new Stops().name("stop3"));
        List<Stops> stops = stopsarr;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Stops (name) VALUES(?)",
                        stops,
                        10,
                        (ps, stopp) -> {
                            ps.setString(1, stopp.getName());
                        }
                );

//        ArrayList<Routes> routesar = new ArrayList<>();
//        routesar.add(new Routes().id(2));
//        routesar.add(new Routes().id(2));
//        routesar.add(new Routes().id(2));
//        List<Routes> routess = routesar;

        ArrayList<Stops> stopsar = new ArrayList<>();
        stopsar.add(new Stops().id(1));
        stopsar.add(new Stops().id(2));
        stopsar.add(new Stops().id(3));
        List<Stops> stopss = stopsar;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO RoutesStops (Rid, Sid) VALUES (?, ?)",
                        stopss,
                        10,
                        (ps, stopsss) -> {
                            ps.setInt(1, 2);
                            ps.setInt(2, stopsss.getId());
                        }
                );



        int routeid = 2;
        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("RouteId", routeid);
        HashMap response = postMethod("/route/get", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
//        System.out.println(response.get("routes"));
//        System.out.println(response.get("routesstops"));
//        System.out.println(response.get("stops"));

    }

    @Test
    public void getRoutes() throws Exception {
        ArrayList<Routes> routesarr = new ArrayList<>();
        routesarr.add(new Routes().name("route1"));
        routesarr.add(new Routes().name("route2"));
        routesarr.add(new Routes().name("route3"));
        List<Routes> routes = routesarr;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Routes (name) VALUES(?)",
                        routes,
                        10,
                        (ps, routee) -> {
                            ps.setString(1, routee.getName());
                        }
                );
        ArrayList<Stops> stopsarr = new ArrayList<>();
        stopsarr.add(new Stops().name("stop1"));
        stopsarr.add(new Stops().name("stop2"));
        stopsarr.add(new Stops().name("stop3"));
        List<Stops> stops = stopsarr;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Stops (name) VALUES(?)",
                        stops,
                        10,
                        (ps, stopp) -> {
                            ps.setString(1, stopp.getName());
                        }
                );


        ArrayList<Stops> stopsar = new ArrayList<>();
        stopsar.add(new Stops().id(1));
        stopsar.add(new Stops().id(2));
        stopsar.add(new Stops().id(3));
        List<Stops> stopss = stopsar;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO RoutesStops (Rid, Sid) VALUES (?, ?)",
                        stopss,
                        10,
                        (ps, stopsss) -> {
                            ps.setInt(1, 2);
                            ps.setInt(2, stopsss.getId());
                        }
                );

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO RoutesStops (Rid, Sid) VALUES (?, ?)",
                        stopss,
                        10,
                        (ps, stopsss) -> {
                            ps.setInt(1, 1);
                            ps.setInt(2, stopsss.getId());
                        }
                );
        HashMap response = getMethod("/route/get_all", HashMap.class);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("routes"));
        System.out.println(response.get("stops"));

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////Route TEST FUNCTION END//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////Schedule TEST FUNCTION START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void createSchedule() throws Exception {
        int routeid = 2;
        int trainid = 1;
        String time = "1641804861";

//        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
//        String currenttime = time.format(new Date(date*1000));

//        System.out.println(currenttime);
        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("RouteId", routeid);
        payload.put("TrainId", trainid);
        payload.put("Time", time);
        HashMap response = postMethod("/schedule/create", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("schedules"));
    }

    @Test
    public void updatetimeSchedule() throws Exception {
        String time = "1641808159";
        int scheduleid = 1;

        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("ScheduleId", scheduleid);
        payload.put("Time", time);


        List<Schedules> schedules = new ArrayList<>();
        Schedules schedule = new Schedules();
        schedule.setRid(1);
        schedule.setTid(1);
        schedule.setTime("13:03");
        schedules.add(schedule);

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Schedules (Rid, Tid, time) VALUES (?, ?, ?)",
                        schedules,
                        10,
                        (ps, schedulee) -> {
                            ps.setInt(1, schedulee.getRid());
                            ps.setInt(2, schedulee.getTid());
                            ps.setString(3, schedulee.getTime());
                            System.out.println(ps);
                        }
                );
        HashMap response = postMethod("/schedule/modify/change_time", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("schedules"));
    }


    @Test
    public void updaterouteSchedule() throws Exception {
        int scheduleid = 1;
        int routeid = 2;


        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("ScheduleId", scheduleid);
        payload.put("RouteId", routeid);


        List<Schedules> schedules = new ArrayList<>();
        Schedules schedule = new Schedules();
        schedule.setRid(1);
        schedule.setTid(1);
        schedule.setTime("13:03");
        schedules.add(schedule);

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Schedules (Rid, Tid, time) VALUES (?, ?, ?)",
                        schedules,
                        10,
                        (ps, schedulee) -> {
                            ps.setInt(1, schedulee.getRid());
                            ps.setInt(2, schedulee.getTid());
                            ps.setString(3, schedulee.getTime());
                            System.out.println(ps);
                        }
                );
        HashMap response = postMethod("/schedule/modify/change_route", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("schedules"));
    }


    @Test
    public void updatetrainSchedule() throws Exception {
        int scheduleid = 1;
        int trainid = 5;


        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("ScheduleId", scheduleid);
        payload.put("TrainId", trainid);


        List<Schedules> schedules = new ArrayList<>();
        Schedules schedule = new Schedules();
        schedule.setRid(1);
        schedule.setTid(1);
        schedule.setTime("13:03");
        schedules.add(schedule);

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Schedules (Rid, Tid, time) VALUES (?, ?, ?)",
                        schedules,
                        10,
                        (ps, schedulee) -> {
                            ps.setInt(1, schedulee.getRid());
                            ps.setInt(2, schedulee.getTid());
                            ps.setString(3, schedulee.getTime());
                            System.out.println(ps);
                        }
                );
        HashMap response = postMethod("/schedule/modify/change_train", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
//        System.out.println(response.get("schedules"));
    }

    @Test
    public void addbbackupSchedule() throws Exception {
        int scheduleid = 1;
        int trainid = 5;


        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("ScheduleId", scheduleid);
        payload.put("TrainId", trainid);


//        List<Schedules> schedules = new ArrayList<>();
//        Schedules schedule = new Schedules();
//        schedule.setRid(1);
//        schedule.setTid(1);
//        schedule.setTime("13:03");
//        schedules.add(schedule);
//
//        Objects.requireNonNull(jdbcTemplate)
//                .batchUpdate(
//                        "INSERT INTO Schedules (Rid, Tid, time) VALUES (?, ?, ?)",
//                        schedules,
//                        10,
//                        (ps, schedulee) -> {
//                            ps.setInt(1, schedulee.getRid());
//                            ps.setInt(2, schedulee.getTid());
//                            ps.setString(3, schedulee.getTime());
//                            System.out.println(ps);
//                        }
//                );
        HashMap response = postMethod("/schedule/modify/add_backup_train", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("backuptrain"));
    }


    @Test
    public void removebbackupSchedule() throws Exception {
        int scheduleid = 1;
        int trainid = 5;


        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("ScheduleId", scheduleid);
        payload.put("TrainId", trainid);


//        List<Schedules> schedules = new ArrayList<>();
//        Schedules schedule = new Schedules();
//        schedule.setRid(1);
//        schedule.setTid(1);
//        schedule.setTime("13:03");
//        schedules.add(schedule);
//
//        Objects.requireNonNull(jdbcTemplate)
//                .batchUpdate(
//                        "INSERT INTO Schedules (Rid, Tid, time) VALUES (?, ?, ?)",
//                        schedules,
//                        10,
//                        (ps, schedulee) -> {
//                            ps.setInt(1, schedulee.getRid());
//                            ps.setInt(2, schedulee.getTid());
//                            ps.setString(3, schedulee.getTime());
//                            System.out.println(ps);
//                        }
//                );
        HashMap response = postMethod("/schedule/modify/remove_backup_train", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("backuptrain"));
    }

    @Test
    public void deleteSchedule() throws Exception {
//        ArrayList<Routes> routesarr = new ArrayList<>();
//        routesarr.add(new Routes().name("route1"));
//        routesarr.add(new Routes().name("route2"));
//        routesarr.add(new Routes().name("route3"));
//        List<Routes> routes = routesarr;
//
//        Objects.requireNonNull(jdbcTemplate)
//                .batchUpdate(
//                        "INSERT INTO Routes (name) VALUES(?)",
//                        routes,
//                        10,
//                        (ps, routee) -> {
//                            ps.setString(1, routee.getName());
//                        }
//                );
        int scheduleid = 2;
        HashMap<String, Integer> payload = new HashMap<String, Integer>();
        payload.put("ScheduleId", scheduleid);
        HashMap response = postMethod("/schedule/delete", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
//        System.out.println(response.get("routes"));
    }

    @Test
    public void getSchedules() throws Exception {
//        ArrayList<Trains> trainsarr = new ArrayList<>();
//        trainsarr.add(new Trains().name("train1"));
//        trainsarr.add(new Trains().name("train2"));
//        trainsarr.add(new Trains().name("train3"));
//        List<Trains> trains = trainsarr;
//
//        Objects.requireNonNull(jdbcTemplate)
//                .batchUpdate(
//                        "INSERT INTO Trains (name) VALUES(?)",
//                        trains,
//                        10,
//                        (ps, trainn) -> {
//                            ps.setString(1, trainn.getName());
//                        }
//                );
        HashMap response = getMethod("/schedule/get_all", HashMap.class);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("schedules"));
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////Stat TEST FUNCTION START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Get all routes that pass from the given stop
    @Test
    public void statKstops() throws Exception {
        ArrayList<Routes> routesarr = new ArrayList<>();
        routesarr.add(new Routes().name("route1"));
        routesarr.add(new Routes().name("route2"));
        routesarr.add(new Routes().name("route3"));
        List<Routes> routes = routesarr;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Routes (name) VALUES(?)",
                        routes,
                        10,
                        (ps, routee) -> {
                            ps.setString(1, routee.getName());
                        }
                );
        ArrayList<Stops> stopsarr = new ArrayList<>();
        stopsarr.add(new Stops().name("stop1"));
        stopsarr.add(new Stops().name("stop2"));
        stopsarr.add(new Stops().name("stop3"));
        List<Stops> stops = stopsarr;
        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Stops (name) VALUES(?)",
                        stops,
                        10,
                        (ps, stopp) -> {
                            ps.setString(1, stopp.getName());
                        }
                );
        ArrayList<Stops> stopsar = new ArrayList<>();
        stopsar.add(new Stops().id(1));
        stopsar.add(new Stops().id(2));
        stopsar.add(new Stops().id(3));
        List<Stops> stopss = stopsar;

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO RoutesStops (Rid, Sid) VALUES (?, ?)",
                        stopss,
                        10,
                        (ps, stopsss) -> {
                            ps.setInt(1, 2);
                            ps.setInt(2, stopsss.getId());
                        }
                );

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO RoutesStops (Rid, Sid) VALUES (?, ?)",
                        stopss,
                        10,
                        (ps, stopsss) -> {
                            ps.setInt(1, 1);
                            ps.setInt(2, stopsss.getId());
                        }
                );
        int stopid = 2;
        HashMap<String, Integer> payload = new HashMap<String,Integer>();
        payload.put("StopId", stopid);
        HashMap response = postMethod("/statistics/route/stop", HashMap.class, payload);
        System.out.println(response.get("routes"));
        System.out.println(response.get("onlyroutesids"));
    }

    ///Get all schedules that start at the given time
    @Test
    public void Schedulestartingiventime() throws Exception {
        String time = "1641808159";

        HashMap<String, String> payload = new HashMap<String, String>();
        payload.put("Time", time);

        List<Schedules> schedules = new ArrayList<>();
        Schedules schedule = new Schedules();
        schedule.setRid(1);
        schedule.setTid(1);
        schedule.setTime("14:49");
        schedules.add(schedule);

        Objects.requireNonNull(jdbcTemplate)
                .batchUpdate(
                        "INSERT INTO Schedules (Rid, Tid, time) VALUES (?, ?, ?)",
                        schedules,
                        10,
                        (ps, schedulee) -> {
                            ps.setInt(1, schedulee.getRid());
                            ps.setInt(2, schedulee.getTid());
                            ps.setString(3, schedulee.getTime());
                            System.out.println(ps);
                        }
                );
        HashMap response = postMethod("/statistics/schedules/time", HashMap.class, payload);
        System.out.println("success: " + response.get("success"));
        System.out.println(response.get("schedules"));
    }

    ///Get all schedules that uses the given route
    @Test
    public void ScheduleuseRoute() throws Exception {
        int routeid = 2;
        HashMap<String, Integer> payload = new HashMap<String,Integer>();
        payload.put("RouteId", routeid);
        HashMap response = postMethod("/statistics/schedules/route", HashMap.class, payload);

    }


}

