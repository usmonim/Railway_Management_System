package edu.ozyegin.cs.controller;

import edu.ozyegin.cs.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping
@CrossOrigin
public class SampleController {
    @Autowired
    private PlatformTransactionManager transactionManager;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    final int batchSize = 10;

    final String createPS = "INSERT INTO Sample (name, data, value) VALUES(?,?,?)";
    final String selectPS = "SELECT * FROM Sample";


    /**
     * Returns a JSON object with "Hello World" message.<br/>
     * Response outline:<br/>
     * <pre>
     *  {
     *      "message": "Hello World",
     *      "status": true
     *  }
     * </pre>
     * */
    @RequestMapping(value = "/sample", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity helloWorld() {
        Map<String, Object> response = new HashMap<>();

        try {
            // response.put("message", "Hello World");
            response.put("success: ", true);
        } catch (Exception ex) {
            response.put("status", false);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Returns a JSON object that echoes received message.<br/>
     * Response outline:<br/>
     * <pre>
     *  {
     *      "echo": " . . . "
     *  }
     * </pre>
     * */
    @RequestMapping(value = "/sample/echo", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity echo(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("echo", payload.get("message"));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
    }

    /**
     * Returns a JSON object with an array of all Samples present in DB.<br/>
     * Response outline:<br/>
     * <pre>
     *  {
     *      "samples": [
     *          { "id": 0, "name": "Some Name", ... },
     *          { "id": 1, "name": "Something", ... },
     *          ...
     *      ],
     *      "status": true
     *  }
     * </pre>
     * */
    @RequestMapping(value = "/sample/entities", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity entities() {
        try {
            List<Sample> data = Objects.requireNonNull(jdbcTemplate).query(selectPS, new BeanPropertyRowMapper<>(Sample.class));

            Map<String, Object> response = new HashMap<>();
            response.put("samples", data);
            response.put("status", true);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
    }

    /**
     * Generates a 'Sample' object to from POST data and inserts into DB.<br/>
     * Returns <code>success</code> status in response data. <code>true</code> if successful, <code>false</code> otherwise<br/>
     * Response outline:<br/>
     * <pre>
     *  {
     *      "success": true
     *  }
     * </pre>
     * */
    @RequestMapping(value = "/sample/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity create(@RequestBody Map<String, Object>[] payload) {
        // prepare data for usage
        List<Sample> samples = new ArrayList<>();
        for (Map<String, Object> entity : payload) {
            Sample sample = new Sample();
            sample.setName((String) entity.get("name"));
            sample.setData((String) entity.get("data"));
            sample.setValue((int) entity.get("value"));

            samples.add(sample);
        }


        // init Transaction Manager
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        // create response's structure
        Map<String, Object> response = new HashMap<>();

        try {
            // INSERT INTO Samples using a PREPARED STATEMENT
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createPS, samples, batchSize,
                    (ps, sample) -> {
                        ps.setString(1, sample.getName());
                        ps.setString(2, sample.getData());
                        ps.setInt(3, sample.getValue());
//                        System.out.println(ps);
                    });

            // commit changes to database
            transactionManager.commit(txStatus);
            response.put("success", true);  // prepare data to respond with
        } catch (Exception exception) {
            // revert changes planned
            transactionManager.rollback(txStatus);

            // prepare data to respond with
            response.put("success", false);
            response.put("message", "Failed inserting Samples");
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
















    /////////////////////////////////////////////////////////////////////////////////////////////////////////STOP ENDPOINTS START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    final String createStopPS = "INSERT INTO Stops (name) VALUES (?)";
    final String selectStopPS = "SELECT * FROM Stops";
    @RequestMapping(value = "/stop/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createStop(@RequestBody Map<String, Object>[] payload) {
        List<Stops> stops = new ArrayList<>();
        for (Map<String, Object> entity : payload) {
            Stops stop = new Stops();
            stop.setName((String) entity.get("name"));
//            System.out.println((String) entity.get("name"));
            stops.add(stop);
        }

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createStopPS, stops, batchSize,
                    (ps, stop) -> {
                        ps.setString(1, stop.getName());
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed inserting Stops");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String updateStopPS = "UPDATE Stops SET name = ? WHERE id = ? ";
    @RequestMapping(value = "/stop/modify/rename", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity updateStop(@RequestBody Map<String, Object> payload) {
        List<Stops> stops = new ArrayList<>();
        Stops stop = new Stops();
        stop.setId((Integer) payload.get("StopId"));
        stop.setName(payload.get("NewName").toString());
        stops.add(stop);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(updateStopPS, stops, batchSize,
                    (ps, stopp) -> {
                        ps.setString(1, stopp.getName());
                        ps.setInt(2, stopp.getId());
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to modify StopName. Check if you have given right input or the Stop do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String deleteStopPS = "DELETE from Stops where id = ?";
    @RequestMapping(value = "/stop/modify/delete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity deleteStop(@RequestBody Map<String, Object>[] payload) {
        List<Stops> stops = new ArrayList<>();
        for (Map<String, Object> entity : payload) {
            Stops stop = new Stops();
            stop.setId((Integer) entity.get("id"));
//            System.out.println((Integer) entity.get("StopId"));
            stops.add(stop);
        }
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(deleteStopPS, stops, batchSize,
                    (ps, stopp) -> {
                        ps.setInt(1, stopp.getId());
                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        } catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed deleting Stop. Check if you have right StopId input or the The Stop with given ID do not exist");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/stop/get_all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getStops() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Stops> data = Objects.requireNonNull(jdbcTemplate).query(selectStopPS, new BeanPropertyRowMapper<>(Stops.class));

            response.put("stops", data);
            response.put("success", true);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////STOP ENDPOINTS END//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////////////Train ENDPOINTS START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    final String createTrainPS = "INSERT INTO Trains (name) VALUES (?)";
    final String selectTrainPS = "SELECT * FROM Trains";
    @RequestMapping(value = "/train/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createTrain(@RequestBody Map<String, Object>[] payload) {
        List<Trains> trains = new ArrayList<>();
        for (Map<String, Object> entity : payload) {
            Trains train = new Trains();
            train.setName((String) entity.get("name"));
//            System.out.println((String) entity.get("name"));
            trains.add(train);
        }

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createTrainPS, trains, batchSize,
                    (ps, train) -> {
                        ps.setString(1, train.getName());
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
            System.out.println("success: " + response.get("success"));
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed inserting Train");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String updateTrainPS = "UPDATE Trains SET name = ? WHERE id = ? ";
    @RequestMapping(value = "/train/modify/rename", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity updateTrain(@RequestBody Map<String, Object> payload) {
        List<Trains> trains = new ArrayList<>();
        Trains train = new Trains();
        train.setId((Integer) payload.get("TrainId"));
        train.setName(payload.get("NewName").toString());
        trains.add(train);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(updateTrainPS, trains, batchSize,
                    (ps, trainn) -> {
                        ps.setString(1, trainn.getName());
                        ps.setInt(2, trainn.getId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to modify TrainName. Check if you have given right input or the Train do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String deleteTrainPS = "DELETE from Trains where id = ?";
    @RequestMapping(value = "/train/modify/delete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity deleteTrain(@RequestBody Map<String, Object>[] payload) {
        List<Trains> trains = new ArrayList<>();
        for (Map<String, Object> entity : payload) {
            Trains train = new Trains();
            train.setId((Integer) entity.get("id"));
            trains.add(train);
        }
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(deleteTrainPS, trains, batchSize,
                    (ps, trainn) -> {
                        ps.setInt(1, trainn.getId());
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        } catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed deleting Train. Check if you have right TrainId input or the The Train with given ID do not exist");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/train/get_all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getTrains() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Trains> data = Objects.requireNonNull(jdbcTemplate).query(selectTrainPS, new BeanPropertyRowMapper<>(Trains.class));

            response.put("trains", data);
            response.put("success", true);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////STOP ENDPOINTS END//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////ROUTE ENDPOINTS START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    final String createRoutePS = "INSERT INTO Routes (name) VALUES (?)";
    final String selectRoutePS = "SELECT * FROM Routes";
    final String createRouteStopPS = "INSERT INTO RoutesStops (Rid, Sid) VALUES (?, ?)";
    final String selectRoutesStopsPS = "SELECT * FROM RoutesStops";
    @RequestMapping(value = "/route/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createRoute(@RequestBody Map<String, Object> payload) {
        List<Routes> routes = new ArrayList<>();
        Routes route = new Routes();
        route.setName((String) payload.get("RouteName"));
        routes.add(route);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createRoutePS, routes, batchSize,
                    (ps, routee) -> {
                        ps.setString(1, routee.getName());
                    });
///////// Here i creatae the stop with random name just because when we insert the Routeid and StopId in RoutesStops table there is foreign key constraint and there should be some stop in Stop table
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createStopPS, routes, batchSize,
                    (ps, stop) -> {
                        ps.setString(1, "stop3245");
                    });

            transactionManager.commit(txStatus);
//prepared statment from geitng id of the inserted Route cuz we need to when we insert it in RoutesStops table
            String selectRoutebyNamePS = selectRoutePS + " where name = '" + (String) payload.get("RouteName") + "'";
            List<Routes> data = Objects.requireNonNull(jdbcTemplate).query(selectRoutebyNamePS, new BeanPropertyRowMapper<>(Routes.class));
            int gotRid = data.get(0).getId();
//List to Store RoutesStops
            List<RoutesStops> routesstops = new ArrayList<>();
            for(int i = 0; i < ((ArrayList) payload.get("stop_ids")).size(); i++){
                RoutesStops routestop = new RoutesStops();
                routestop.setRId(gotRid);
                int gotSid = (Integer) (((ArrayList) payload.get("stop_ids")).get(i));
                routestop.setSId(gotSid);
                routesstops.add(routestop);
            }
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createRouteStopPS, routesstops, batchSize,
                    (ps, routeestopp) -> {
                        ps.setInt(1, routeestopp.getRId());
                        ps.setInt(2, routeestopp.getSId());
                    });

            List<RoutesStops> data1 = Objects.requireNonNull(jdbcTemplate).query(selectRoutesStopsPS, new BeanPropertyRowMapper<>(RoutesStops.class));
            response.put("success", true);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed inserting Route with it Stops. Check if provided name is unique and if the provided stops exist in Stop table");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String updateRoutePS = "UPDATE Routes SET name = ? WHERE id = ? ";
    @RequestMapping(value = "/route/modify/rename", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity updateRoute(@RequestBody Map<String, Object> payload) {
        List<Routes> routes = new ArrayList<>();
        Routes route = new Routes();
        route.setId((Integer) payload.get("RouteId"));
        route.setName(payload.get("NewName").toString());
        routes.add(route);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(updateRoutePS, routes, batchSize,
                    (ps, routee) -> {
                        ps.setString(1, routee.getName());
                        ps.setInt(2, routee.getId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to modify RouteName. Check if you have given right input or the Route do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/route/modify/add_stop", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addStop(@RequestBody Map<String, Integer> payload) {
        List<RoutesStops> routesstops = new ArrayList<>();
        RoutesStops routestop = new RoutesStops();
        routestop.setRId(payload.get("RouteId"));
        routestop.setSId(payload.get("StopId"));
        routesstops.add(routestop);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createRouteStopPS, routesstops, batchSize,
                    (ps, routeestopp) -> {
                        ps.setInt(1, routeestopp.getRId());
                        ps.setInt(2, routeestopp.getSId());
                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to add Stop to ROute. Check if you have given right input or the given Route or Stop id's do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String removeStopPS = "DELETE from RoutesStops where Rid = ? and Sid = ?";
    @RequestMapping(value = "/route/modify/remove_stop", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity removeStop(@RequestBody Map<String, Integer> payload) {
        List<RoutesStops> routesstops = new ArrayList<>();
        RoutesStops routestop = new RoutesStops();
        routestop.setRId(payload.get("RouteId"));
        routestop.setSId(payload.get("StopId"));
        routesstops.add(routestop);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(removeStopPS, routesstops, batchSize,
                    (ps, routeestopp) -> {
                        ps.setInt(1, routeestopp.getRId());
                        ps.setInt(2, routeestopp.getSId());
                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to remove Stop from Route. Check if you have given right input or the given Route or Stop id's do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String deleteRoutePS = "DELETE from Routes where id = ?";
    final String deleteRouteStopPS = "DELETE from RoutesStops where Rid = ?";
    @RequestMapping(value = "/route/delete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity deleteRoute(@RequestBody Map<String, Integer> payload) {
        List<RoutesStops> routesstops = new ArrayList<>();
        RoutesStops routestop = new RoutesStops();
        routestop.setRId(payload.get("RouteId"));
        routesstops.add(routestop);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();
////Deleating first from RoutesStops and then the Route itself  from Routes
        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(deleteRouteStopPS, routesstops, batchSize,
                    (ps, routeestopp) -> {
                        ps.setInt(1, routeestopp.getRId());
//                        System.out.println(ps);
                    });
            Objects.requireNonNull(jdbcTemplate).batchUpdate(deleteRoutePS, routesstops, batchSize,
                    (ps, routeestopp) -> {
                        ps.setInt(1, routeestopp.getRId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
//            List<Routes> data = Objects.requireNonNull(jdbcTemplate).query(selectRoutePS, new BeanPropertyRowMapper<>(Routes.class));
//            response.put("routes", data);
        }

        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to remove Stop from Route. Check if you have given right input or the given Route or Stop id's do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String selectRoutebyIdPS = "SELECT * FROM Routes where id = ";
    final String selectStopsbyIdPS = "Select name from Stops where id = ";

    @RequestMapping(value = "/route/get", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity getRoute(@RequestBody Map<String, Integer> payload) {
        List<RoutesStops> routesstops = new ArrayList<>();
        RoutesStops routestop = new RoutesStops();
        routestop.setRId(payload.get("RouteId"));
        routesstops.add(routestop);

        Map<String, Object> response = new HashMap<>();


        try {
            String takestopsbyRid = "Select Sid from RoutesStops where Rid = ";
            List<Routes> data = Objects.requireNonNull(jdbcTemplate).query(selectRoutebyIdPS + routesstops.get(0).getRId(), new BeanPropertyRowMapper<>(Routes.class));
            List<RoutesStops> data2 = Objects.requireNonNull(jdbcTemplate).query(takestopsbyRid + payload.get("RouteId"), new BeanPropertyRowMapper<>(RoutesStops.class));
            List<Stops> data3 = new ArrayList<>();
            for(int i = 0; i < data2.size(); i++){
                List<Stops> datatemp = Objects.requireNonNull(jdbcTemplate).query(selectStopPS + " where id = " + data2.get(i).getSId(), new BeanPropertyRowMapper<>(Stops.class));
                data3.add(datatemp.get(0));
            }

            response.put("routes", data);
            response.put("stops", data3);
            response.put("routesstops", data2);

            response.put("success", true);

        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to remove Stop from Route. Check if you have given right input or the given Route or Stop id's do not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/route/get_all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getRoutes() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Routes> data = Objects.requireNonNull(jdbcTemplate).query(selectRoutePS, new BeanPropertyRowMapper<>(Routes.class));
            String takestopsbyRid = "Select Sid from RoutesStops where Rid = ";
            List<Stops> data3 = new ArrayList<>();

            for(int i = 0; i < data.size(); i++){
                data.get(i).getId();
                List<RoutesStops> data2 = Objects.requireNonNull(jdbcTemplate).query(takestopsbyRid +  data.get(i).getId(), new BeanPropertyRowMapper<>(RoutesStops.class));
                for(int y = 0; y < data2.size(); y++){
                    List<Stops> datatemp = Objects.requireNonNull(jdbcTemplate).query(selectStopPS + " where id = " + data2.get(y).getSId(), new BeanPropertyRowMapper<>(Stops.class));
                    data3.add(datatemp.get(0));
                }
            }

            response.put("routes", data);
            response.put("stops", data3);
            response.put("success", true);

        }
        catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to get all Routes. Check if you have given right input or the given Route do not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////Route ENDPOINTS END//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////Schedule ENDPOINTS START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    final String createSchedulePS = "INSERT INTO Schedules (Rid, Tid, time) VALUES (?, ?, ?)";
    @RequestMapping(value = "/schedule/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createSchedule(@RequestBody Map<String, Object> payload) {
        long time = Long.parseLong((String) payload.get("Time"));
//        System.out.println(time);
        SimpleDateFormat convert = new SimpleDateFormat("HH:mm");
        String currenttime = convert.format(new Date(time*1000));
//        System.out.println(currenttime);

        List<Schedules> schedules = new ArrayList<>();
        Schedules schedule = new Schedules();
        schedule.setRid((Integer) payload.get("RouteId"));
        schedule.setTid((Integer) payload.get("TrainId"));
        schedule.setTime(currenttime);
        schedules.add(schedule);
//        System.out.println((String) payload.get("Time"));

        Map<String, Object> response = new HashMap<>();

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createSchedulePS, schedules, batchSize,
                    (ps, schedulee) -> {
                        ps.setInt(1, schedulee.getRid());
                        ps.setInt(2, schedulee.getTid());
                        ps.setString(3, schedulee.getTime());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);

            String temp = "Select * from Schedules";
            List<Schedules> data = Objects.requireNonNull(jdbcTemplate).query(temp, new BeanPropertyRowMapper<>(Schedules.class));
            response.put("schedules", data);
//            System.out.println(data.get(0).getTime());
//            System.out.println(schedules.get(0).getTime());

        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to create Schedule. Check if you have given right input or the given Route or Train id's do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String updatetimeSchedulePS = "UPDATE Schedules SET time = ? WHERE id = ? ";
    @RequestMapping(value = "/schedule/modify/change_time", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity updatetimeSchedule(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        //Converting string unix time string human readoble
        long time = Long.parseLong((String) payload.get("Time"));
        SimpleDateFormat convert = new SimpleDateFormat("HH:mm");
        String currenttime = convert.format(new Date(time*1000));

        List<Schedules> schedules = new ArrayList<>();
        Schedules schedule = new Schedules();
        schedule.setId((Integer) payload.get("ScheduleId"));
        schedule.setTime(currenttime);
        schedules.add(schedule);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(updatetimeSchedulePS, schedules, batchSize,
                    (ps, schedulee) -> {
                        ps.setString(1, schedulee.getTime());
                        ps.setInt(2, schedulee.getId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);

//            String temp = "Select * from Schedules";
//            List<Schedules> data = Objects.requireNonNull(jdbcTemplate).query(temp, new BeanPropertyRowMapper<>(Schedules.class));
//            response.put("schedules", data);

        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to update time of Schedule. Check if you have given right input or the given Schedule id do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String updaterouteSchedulePS = "UPDATE Schedules SET Rid = ? WHERE id = ? ";
    @RequestMapping(value = "/schedule/modify/change_route", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity updaterouteSchedule(@RequestBody Map<String, Integer> payload) {
        Map<String, Object> response = new HashMap<>();

        List<Schedules> schedules = new ArrayList<>();
        Schedules schedule = new Schedules();
        schedule.setId((Integer) payload.get("ScheduleId"));
        schedule.setRid((Integer) payload.get("RouteId"));
        schedules.add(schedule);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(updaterouteSchedulePS, schedules, batchSize,
                    (ps, schedulee) -> {
                        ps.setInt(1, schedulee.getRid());
                        ps.setInt(2, schedulee.getId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);

//            String temp = "Select * from Schedules";
//            List<Schedules> data = Objects.requireNonNull(jdbcTemplate).query(temp, new BeanPropertyRowMapper<>(Schedules.class));
//            response.put("schedules", data);

        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to update route of Schedule. Check if you have given right route if input or the given Schedule or Route id do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    final String updatetrainSchedulePS = "UPDATE Schedules SET Tid = ? WHERE id = ? ";
    @RequestMapping(value = "/schedule/modify/change_train", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity updatetrainSchedule(@RequestBody Map<String, Integer> payload) {
        Map<String, Object> response = new HashMap<>();

        List<Schedules> schedules = new ArrayList<>();
        Schedules schedule = new Schedules();
        schedule.setId((Integer) payload.get("ScheduleId"));
        schedule.setTid((Integer) payload.get("TrainId"));
        schedules.add(schedule);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(updatetrainSchedulePS, schedules, batchSize,
                    (ps, schedulee) -> {
                        ps.setInt(1, schedulee.getTid());
                        ps.setInt(2, schedulee.getId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);

//            String temp = "Select * from Schedules";
//            List<Schedules> data = Objects.requireNonNull(jdbcTemplate).query(temp, new BeanPropertyRowMapper<>(Schedules.class));
//            response.put("schedules", data);

        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to update train of Schedule. Check if you have given right input or the given Schedule or Train id do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String createBackupTrainPS = "INSERT INTO BackupTrains (sid, Tid) VALUES (?, ?)";
    @RequestMapping(value = "/schedule/modify/add_backup_train", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addbbackupSchedule(@RequestBody Map<String, Integer> payload) {
        List<BackupTrains> backuptrains = new ArrayList<>();
        BackupTrains backuptrain = new BackupTrains();
        backuptrain.setSId(payload.get("ScheduleId"));
        backuptrain.setTId(payload.get("TrainId"));
        backuptrains.add(backuptrain);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(createBackupTrainPS, backuptrains, batchSize,
                    (ps, backuptrainn) -> {
                        ps.setInt(1, backuptrainn.getSId());
                        ps.setInt(2, backuptrainn.getTId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
//            String temp = "Select * from BackupTrains";
//            List<BackupTrains> data = Objects.requireNonNull(jdbcTemplate).query(temp, new BeanPropertyRowMapper<>(BackupTrains.class));
//            response.put("backuptrain", data);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to add back up Train to Schedule. Check if you have given right input or the given Schedule or Train id's do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }


    final String removeBackupTrainPS = "DELETE from BackupTrains where sid = ? and Tid = ?";
    @RequestMapping(value = "/schedule/modify/remove_backup_train", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity removebbackupSchedule(@RequestBody Map<String, Integer> payload) {
        List<BackupTrains> backuptrains = new ArrayList<>();
        BackupTrains backuptrain = new BackupTrains();
        backuptrain.setSId(payload.get("ScheduleId"));
        backuptrain.setTId(payload.get("TrainId"));
        backuptrains.add(backuptrain);

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();

        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(removeBackupTrainPS, backuptrains, batchSize,
                    (ps, backuptrainn) -> {
                        ps.setInt(1, backuptrainn.getSId());
                        ps.setInt(2, backuptrainn.getTId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
//            String temp = "Select * from BackupTrains";
//            List<BackupTrains> data = Objects.requireNonNull(jdbcTemplate).query(temp, new BeanPropertyRowMapper<>(BackupTrains.class));
//            response.put("backuptrain", data);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to remove back up Train of Schedule. Check if you have given right input or the given Schedule or Train id's do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    final String deleteSchedulePS = "DELETE from Schedules where id = ?";
    final String deleteBackupTrainPS = "DELETE  from BackupTrains where sid = ?";
    @RequestMapping(value = "/schedule/delete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity deleteSchedule(@RequestBody Map<String, Integer> payload) {
        List<BackupTrains> backuptrains = new ArrayList<>();
        BackupTrains backuptrain = new BackupTrains();
        backuptrain.setSId(payload.get("ScheduleId"));
        backuptrains.add(backuptrain);


        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Map<String, Object> response = new HashMap<>();
//Deleting first from BackupTrains and then the Schedules
        try {
            Objects.requireNonNull(jdbcTemplate).batchUpdate(deleteBackupTrainPS, backuptrains, batchSize,
                    (ps, backuptrainn) -> {
                        ps.setInt(1, backuptrainn.getSId());
//                        System.out.println(ps);
                    });
            Objects.requireNonNull(jdbcTemplate).batchUpdate(deleteSchedulePS, backuptrains, batchSize,
                    (ps, backuptrainn) -> {
                        ps.setInt(1, backuptrainn.getSId());
//                        System.out.println(ps);
                    });
            transactionManager.commit(txStatus);
            response.put("success", true);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to remove Schedule. Check if you have given right input or the given Schedule do not exist.");
            System.out.println("message: " + response.get("message"));
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String selectSchedulesPS = "Select * from Schedules";
    @RequestMapping(value = "/schedule/get_all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSchedules() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Schedules> data = Objects.requireNonNull(jdbcTemplate).query(selectSchedulesPS, new BeanPropertyRowMapper<>(Schedules.class));

            response.put("schedules", data);
            response.put("success", true);

        } catch (Exception ex) {
            System.out.println("No Schedules data exists");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////Stat ENDPOINTS START//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    @RequestMapping(value = "/statistics/route/stop", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity statKstops(@RequestBody Map<String, Integer> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            String takestopsbyRid = "select Rid from RoutesStops where Sid = ";
            List<RoutesStops> data1 = Objects.requireNonNull(jdbcTemplate).query(takestopsbyRid + payload.get("StopId"), new BeanPropertyRowMapper<>(RoutesStops.class));
            List<Integer> onlyids = new ArrayList<>();
            for (int i = 0; i< data1.size(); i++) {
                onlyids.add(data1.get(i).getRId());
            }
            response.put("routes", data1);
            response.put("success", true);
            response.put("onlyroutesids", onlyids);
        }
        catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to find Routes that pass from the given Stop. Check if you have given right input or the given Route or Stop id's do not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    final String SchedulestartsgivenTimePS = "Select * from Schedules where time = ";
    @RequestMapping(value = "/statistics/schedules/time", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity Schedulestartingiventime(@RequestBody Map<String, String> payload) {
        long time = Long.parseLong((String) payload.get("Time"));
//        System.out.println(time);
        SimpleDateFormat convert = new SimpleDateFormat("HH:mm");
        String currenttime = convert.format(new Date(time*1000));
        Map<String, Object> response = new HashMap<>();

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        try {
            List<Schedules> data = Objects.requireNonNull(jdbcTemplate).query(SchedulestartsgivenTimePS + "'" + currenttime + "'", new BeanPropertyRowMapper<>(Schedules.class));
            response.put("success", true);
            response.put("schedules", data);
        }
        catch (Exception exception) {
            transactionManager.rollback(txStatus);
            response.put("success", false);
            response.put("message", "Failed to Get schedules that start at the given time. Check if you have given right input or Schedule do not exist.");
        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @RequestMapping(value = "/statistics/schedules/route", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity ScheduleuseRoute(@RequestBody Map<String, Integer> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            String takeschedulesbyRid = "select * from Schedules where Rid = ";
            List<Schedules> data = Objects.requireNonNull(jdbcTemplate).query(takeschedulesbyRid + payload.get("RouteId"), new BeanPropertyRowMapper<>(Schedules.class));

            response.put("routes", data);
            response.put("success", true);
        }
        catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to find schedules that uses the given route. Check if you have given right input or the given Route or Stop id's do not exist.");
            System.out.println(response.get("message"));
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

        }
        System.out.println("success: " + response.get("success"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



}