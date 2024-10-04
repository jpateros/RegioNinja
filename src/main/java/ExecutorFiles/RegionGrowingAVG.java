package ExecutorFiles;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.awt.*;
import java.util.ArrayList;
import java.util.*;
import java.util.List;
import java.util.Random;

import static ExecutorFiles.PolygonGraph.printPolygons;
import static ExecutorFiles.RegionGrowing.doPolygonsOverlap;

public class RegionGrowingAVG {
    public static void main(String[] args) {

        //initlaize the seeds and graph that makes up the area for the region growing average testing
        Set<Area> seeds = new HashSet<>();
        MutableGraph<Area> empPaperExample = createInputGraph(seeds);



    }

    public static List<Set<Area>> regionGrowingAvg(Set<Area> seedAreas, double low, double high, MutableGraph<Area> regions) {
        //making assumption that substep 2.1 is already done for min max filtering
        List<Set<Area>> regionListP = null;

        Set<Area> unassigned_low = new HashSet<>();
        Set<Area> unassigned_high = new HashSet<>();
        Set<Area> unassigned_average = new HashSet<>();
        //classify the seeds properly
        for (Area seed : seedAreas) {
            double spatialAttribute = seed.getSpatiallyExtensiveAttribute();
            if (spatialAttribute < low) {
                unassigned_low.add(seed);
            } else if (spatialAttribute > high) {
                unassigned_high.add(seed);
            } else {
                unassigned_average.add(seed);
            }
        }

       regionListP = unAssignedAreasAlgo(low, high, unassigned_low, unassigned_high, regions);




        return regionListP;
    }

    public static List<Set<Area>> unAssignedAreasAlgo(double low, double u, Set<Area> unassigned_low, Set<Area> unassigned_high, MutableGraph<Area> allArea)
    {
        List<Set<Area>> finalRegions = new ArrayList<>();
        Set<Area> u_areas = union(unassigned_low, unassigned_high);
        Set<Area> removed_areas = new HashSet<>();
        boolean updated = false;
        for (Area area : u_areas)
        {
            Set<Area> regionR = new HashSet<>();
            regionR.add(area);
            updated = true;

            Set<Area> neighbor_areas = getNeighbors(regionR, allAreas);
            while (updated)
            {
                updated = false;
                if (low <=  getRegionAverage(regionR) && getRegionAverage(regionR) <= u) {
                    finalRegions.add(regionR);
                    removed_areas.addAll(regionR);
                }
                else {
                    for (Area neighbor : neighbor_areas)
                    {
                        if (!neighbor.getBelongsToRegion())
                        {
                            double regionAvg = getRegionAverage(regionR);
                            if (regionAvg < low && neighbor.getDissimilarityAttribute() > u || (regionAvg > u && neighbor.getDissimilarityAttribute() < low))
                                regionR.add(neighbor);
                                updated = true;
                                break;
                            }
                        }
                    }
                }
            }

        return finalRegions;
    }

    public static double getRegionAverage(Set<Area> regions)
    {
        double sum = 0;
        for (Area current : regions)
        {
            sum += current.getSpatiallyExtensiveAttribute();
        }
        return sum / regions.size();
    }

    public static Set<Area> getNeighbors(Set<Area> interest, MutableGraph<Area> totalAreas)
    {
        Set<Area> neighbors = new HashSet<>();
        for (Area regionArea : interest) {
            for (Area testOutsideArea : totalAreas.nodes()) {
                if (doPolygonsOverlap(regionArea.getPolygon(), testOutsideArea.getPolygon()))
                {
                    neighbors.add(testOutsideArea);
                }
            }

        }
        return neighbors;
    }

    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {

        Set<T> unionSet = new HashSet<>(set1);
        unionSet.addAll(set2);

        return unionSet;
    }


    public static boolean satisifiesConstraint(Area testArea, double low, double high) {
        double attribute = testArea.getSpatiallyExtensiveAttribute();
        return attribute > low && attribute < high;
    }


    public static MutableGraph<Area> createInputGraph(Set<Area> seeds) {

        MutableGraph<Area> graph = GraphBuilder.undirected().build();

        int[] xCoords = {1, 4, 1, 4};
        int[] yCoords = {0, 2, 2, 0};
        Polygon dummyPoly = new Polygon(xCoords, yCoords, 4);
        Area a1 = new Area(1, dummyPoly, 1, 11);
        Area a2 = new Area(2, dummyPoly, 2, 12);
        Area a3 = new Area(3, dummyPoly, 3, 13);
        Area a4 = new Area(4, dummyPoly, 4, 14);
        Area a5 = new Area(5, dummyPoly, 5, 15);
        Area a6 = new Area(6, dummyPoly, 6, 16);
        Area a7 = new Area(7, dummyPoly, 7, 17);
        Area a8 = new Area(8, dummyPoly, 8, 18);
        Area a9 = new Area(9, dummyPoly, 9, 19);

        graph.addNode(a1);
        graph.addNode(a2);
        graph.addNode(a3);
        graph.addNode(a4);
        graph.addNode(a5);
        graph.addNode(a6);
        graph.addNode(a7);
        graph.addNode(a8);
        graph.addNode(a9);
        //a1
        graph.putEdge(a1, a2);
        graph.putEdge(a1, a3);
        graph.putEdge(a1, a4);
        //a2
        graph.putEdge(a2, a1);
        graph.putEdge(a2, a4);
        graph.putEdge(a2, a5);
        graph.putEdge(a2, a6);
        //a3
        graph.putEdge(a3, a1);
        graph.putEdge(a3, a4);
        graph.putEdge(a3, a7);
        //a4
        graph.putEdge(a4, a1);
        graph.putEdge(a4, a2);
        graph.putEdge(a4, a6);
        graph.putEdge(a4, a9);
        graph.putEdge(a4, a7);
        graph.putEdge(a4, a3);
        //a5
        graph.putEdge(a5, a2);
        graph.putEdge(a5, a6);
        graph.putEdge(a5, a8);
        //a6
        graph.putEdge(a6, a2);
        graph.putEdge(a6, a5);
        graph.putEdge(a6, a9);
        graph.putEdge(a6, a4);
        //a7
        graph.putEdge(a7, a3);
        graph.putEdge(a7, a4);
        graph.putEdge(a7, a9);
        //a8
        graph.putEdge(a8, a5);
        //a9
        graph.putEdge(a9, a6);
        graph.putEdge(a9, a4);
        graph.putEdge(a9, a7);

        //min seeds
        seeds.add(a2);
        seeds.add(a3);
        seeds.add(a4);

        //max seeds
        seeds.add(a6);
        seeds.add(a7);

        //TODO Do I need tto have the min and max seeds in different sets?

        return graph;
    }

}
