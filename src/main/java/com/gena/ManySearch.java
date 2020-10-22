package com.gena;

import com.gena.service.City;

import javafx.util.Pair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ManySearch {
    public static void main(String[] args) throws IOException {
        final Path waysData = Paths.get( "src/main/resources/CitesWays.txt");
        HashMap<String, City> citesMap = makeCitesMap(waysData);

        final Path coordData = Paths.get( "src/main/resources/CitesCoord.txt");
        fillCoordData(coordData, citesMap);

        printAns(bfs(citesMap.get("Рига"), citesMap.get("Уфа")),
                citesMap.get("Рига"),
                citesMap.get("Уфа"));

        printAns(dfs(citesMap.get("Рига"), citesMap.get("Уфа")),
                citesMap.get("Рига"),
                citesMap.get("Уфа"));

        printAns(dfsWithDepthLimitation(citesMap.get("Рига"), citesMap.get("Уфа"), 16),
                citesMap.get("Рига"),
                citesMap.get("Уфа"));

        printAns(dfsWithIterationDepth(citesMap.get("Рига"), citesMap.get("Уфа")),
                citesMap.get("Рига"),
                citesMap.get("Уфа"));

        printAns(bidirectionalSearch(citesMap.get("Рига"), citesMap.get("Уфа")),
                citesMap.get("Рига"),
                citesMap.get("Уфа"));

        fillLengthToGoal(citesMap, citesMap.get("Уфа"));

        printAns(greedySearch(citesMap.get("Рига"), citesMap.get("Уфа")),
                citesMap.get("Рига"),
               citesMap.get("Уфа"));

        printAns(aSearch(citesMap.get("Рига"), citesMap.get("Уфа")),
                citesMap.get("Рига"),
                citesMap.get("Уфа"));
    }

    public static HashMap<String, City> makeCitesMap(Path resS) throws IOException{
        List<String> listArr = Files.readAllLines(resS, Charset.forName("windows-1251"));
        String[][] citesdata = Arrays.stream(listArr.toArray())
                .map(s -> s.toString().split(" ")).toArray(String[][]::new);

        HashMap<String, City> citesMap = new HashMap<>();

        for (String[] citesdatum : citesdata) {
            String A = citesdatum[0];
            String B = citesdatum[1];
            int distance = Integer.parseInt(citesdatum[2]);

            //System.out.println(A + ' ' + B + ' ' + distance + '\n');

            if (!citesMap.containsKey(A)) citesMap.put(A, new City(A));
            if (!citesMap.containsKey(B)) citesMap.put(B, new City(B));

            citesMap.get(A).addWay(citesMap.get(B), distance);
        }
        return citesMap;
    }

    public static void fillCoordData(Path resS, HashMap<String, City> citesMap) throws IOException {
        List<String> listArr = Files.readAllLines(resS, Charset.forName("windows-1251"));
        String[][] citesdata = Arrays.stream(listArr.toArray())
                .map(s -> s.toString().split(" ")).toArray(String[][]::new);
        for (String[] citesdatum : citesdata) {
            String A = citesdatum[0];
            double x = Double.parseDouble(citesdatum[1]);
            double y = Double.parseDouble(citesdatum[2]);

            if (citesMap.containsKey(A)){
                citesMap.get(A).setCoord(x, y);
            }
        }
    }

    public static void fillLengthToGoal(HashMap<String, City> citesMap, City goal){
        for (City c : citesMap.values()){
            if (c.x == 0){
                System.out.println(c.name);
            }
            c.setLengthToGoal(goal);
            System.out.println(c.name + " " + c.lengthToGoal);
        }
    }

    public static HashMap<City, City> bfs(City start,
                              City finish){

        HashMap<City, City> visited = new HashMap<>();

        ArrayDeque<City> q = new ArrayDeque<>();

        q.addLast(start);
        visited.put(start, start);
        boolean done = false;

        while(!q.isEmpty() && !done){
            City pos = q.pollFirst();
            for (Pair<City, Integer> way : pos.ways){
                if (!visited.containsKey(way.getKey())) {
                    visited.put(way.getKey(), pos);
                    //System.out.println(pos.name + "->" + way.getKey().name + " ");
                    q.addLast(way.getKey());
                    if (way.getKey() == finish) {
                        done = true;
                    }
                }
            }
        }
        if (done){
            return visited;
        }else{
            return null;
        }
    }

    public static HashMap<City, City> dfs(City pos,
                                     City goal){
        HashMap<City, City> visited = new HashMap<>();
        visited.put(pos, pos);
        if (dfsWithDepthLimitationProcess(pos, goal, visited, -1)){
            return visited;
        }else{
            return null;
        }
    }

    public static HashMap<City, City> dfsWithDepthLimitation(City pos,
                                                       City goal,
                                                       int limit){
        HashMap<City, City> visited = new HashMap<>();
        visited.put(pos, pos);
        if (dfsWithDepthLimitationProcess(
                pos,
                goal,
                visited,
                limit)){
            return visited;
        }else{
            return null;
        }
    }

    public static HashMap<City, City> dfsWithIterationDepth(City pos,
                                                            City goal){
        boolean success = false;
        int limit = 1;
        while (limit < 40){
            HashMap<City, City> visited = new HashMap<>();
            visited.put(pos, pos);
            success = dfsWithDepthLimitationProcess(
                    pos,
                    goal,
                    visited,
                    limit);
            if (success){
                System.out.println(limit);
                return visited;
            }
            limit++;
        }
        return null;
    }

    public static boolean dfsWithDepthLimitationProcess(City pos,
                                                        City goal,
                                                        HashMap<City, City> visited,
                                                        int depth){
        depth--;
        if (pos == goal) return true;
        if (depth != 0) {
            for (Pair<City, Integer> way : pos.ways) {
                if (!visited.containsKey(way.getKey())) {

                    visited.put(way.getKey(), pos);

                    //System.out.println(pos.name + "->" + way.getKey().name + " ");

                    if (dfsWithDepthLimitationProcess(
                            way.getKey(),
                            goal,
                            visited,
                            depth)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static HashMap<City, City> bidirectionalSearch(City start, City finish){

        HashMap<City, City> visited1 = new HashMap<>();
        ArrayDeque<City> q1 = new ArrayDeque<>();

        q1.addLast(start);
        visited1.put(start, start);

        HashMap<City, City> visited2 = new HashMap<>();
        ArrayDeque<City> q2 = new ArrayDeque<>();

        q2.addLast(finish);
        visited2.put(finish, finish);

        boolean done = false;

        City pos = null;
        while(!q1.isEmpty() && !q2.isEmpty() && !done){
            pos = q1.pollFirst();
            pos = keyNode(visited1, visited2, q1, pos);
            done = (pos != null);

            if (!done) {
                pos = q2.pollFirst();
                pos = keyNode(visited2, visited1, q2, pos);
                done = (pos != null);
            }
        }

        if (!done) return null;
//*
        while (pos != finish){
            visited1.put(visited2.get(pos), pos);
            pos = visited2.get(pos);
        }//*/
        return visited1;
    }

    private static City keyNode(HashMap<City, City> visited1,
                                HashMap<City, City> visited2,
                                ArrayDeque<City> q,
                                City pos) {
        //System.out.println(pos.name);
        for (Pair<City, Integer> way : pos.ways){
            if (!visited1.containsKey(way.getKey())) {
                visited1.put(way.getKey(), pos);
                //System.out.println(pos.name + "->" + way.getKey().name + " ");
                if (!visited2.containsKey(way.getKey())){
                    q.addLast(way.getKey());
                }else{
                    return way.getKey();
                }
            }
        }
        return null;
    }

    public static HashMap<City, City> greedySearch(City start, City finish){
        HashMap<City, City> visited = new HashMap<>();
        visited.put(start, start);
        Comparator<Pair<City, Integer>> usingCom = Comparator.comparingInt(o -> o.getKey().lengthToGoal);
        if (informedSearchProcess(start, finish, visited, usingCom)){
            return visited;
        }else{
            return null;
        }
    }

    public static HashMap<City, City> aSearch(City start, City finish){
        HashMap<City, City> visited = new HashMap<>();
        visited.put(start, start);
        Comparator<Pair<City, Integer>> usingCom = Comparator.comparingInt(o -> (o.getKey().lengthToGoal + o.getValue()));
        if (informedSearchProcess(start, finish, visited, usingCom)){
            return visited;
        }else{
            return null;
        }
    }

    public static boolean informedSearchProcess(City pos,
                                                City goal,
                                                HashMap<City, City> visited,
                                                Comparator<Pair<City, Integer>> usingCom){
        if (pos == goal) {
            return true;
        }else{
            //System.out.println(pos.name);
            PriorityQueue<Pair<City, Integer>> ways = new PriorityQueue<>(pos.ways.size(), usingCom);
            for (Pair<City, Integer> way : pos.ways)
                if (!visited.containsKey(way.getKey())) {
                    ways.add(way);
                }

            for (int i = 0; i < ways.size(); i++) {
                Pair<City, Integer> way = ways.poll();
                if (!visited.containsKey(way.getKey())) {
                    visited.put(way.getKey(), pos);
                    System.out.println(pos.name + "->" + way.getKey().name + " " + (way.getKey().lengthToGoal + way.getValue()));
                    if (informedSearchProcess(
                            way.getKey(),
                            goal,
                            visited,
                            usingCom)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void printAns(HashMap<City, City> ways, City start, City finish){
        if (ways != null){
            StringBuilder ans = new StringBuilder();
            City pos = finish;
            while (pos != start){
                ans.append(pos.name).append("<-");
                pos = ways.get(pos);
            }
            ans.append(pos.name);
            System.out.println(ans.toString());
        }else{
            System.out.println("fuck!");
        }
    }
}
