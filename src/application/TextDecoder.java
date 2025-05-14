package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TextDecoder
{
    ArrayList<Cell> cells = new ArrayList<Cell>();
    int[] waveDelays;
    int[] enemyCountPerWave;
    double[] enemySpawnDelayPerWave;
    String[] rows;

    public TextDecoder(File levelFile)
    {
        rows = getLines(levelFile);
        int width = Integer.parseInt((rows[0].split(":"))[1]);
        int height = Integer.parseInt((rows[1].split(":"))[1]);
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                cells.add(new Cell(i, j, 70, false));
            }
        }
        waveDelays = getDelayToOtherWave();
        enemyCountPerWave = getEnemyCountPerWave();
        enemySpawnDelayPerWave = getEnemySpawnDelayPerWave();
    }

    static String[] getLines(File levelFile)
    {
        ArrayList<String> lines = new ArrayList<>();
        try
        {
            Scanner scan = new Scanner(levelFile);
            while(scan.hasNext())
            {
                lines.add(scan.nextLine());
            }
            scan.close();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        return lines.toArray(new String[0]);
    }

    public int[] getEnemyCountPerWave()
    {
        ArrayList<Integer> waveData = new ArrayList<>();
        for (int i = 15; i < rows.length; i++)
        {
            String[] row = rows[i].split(",");
            if (row.length == 3)
            {
                String enemyCount = row[0].trim();
                waveData.add(Integer.parseInt(enemyCount));
            }
        }
        return waveData.stream().mapToInt(Integer::intValue).toArray();
    }
    public double[] getEnemySpawnDelayPerWave()
    {
        ArrayList<Double> waveData = new ArrayList<>();
        for (int i = 15; i < rows.length; i++)
        {
            String[] row = rows[i].split(",");
            if (row.length == 3)
            {
                String spawnDelay = row[1].trim();
                waveData.add(Double.parseDouble(spawnDelay));
            }
        }
        return waveData.stream().mapToDouble(Double::doubleValue).toArray();
    }
    public int[] getDelayToOtherWave()
    {
        ArrayList<Integer> waveData = new ArrayList<>();
        for (int i = 15; i < rows.length; i++)
        {
            String[] row = rows[i].split(",");
            if (row.length == 3)
            {
                String delay = row[2].trim();
                waveData.add(Integer.parseInt(delay));
            }
        }
        return waveData.stream().mapToInt(Integer::intValue).toArray();
    }

 // Fix in TextDecoder class - getPathCells() method
    public ArrayList<Cell> getPathCells() {
        ArrayList<Cell> pathCells = new ArrayList<>();
        for (int i = 2; i < rows.length; i++) {
            if(rows[i].equals("WAVE_DATA:")) {
                break;
            }
            else {
                String[] coordinates = rows[i].split(",");
                if(coordinates.length >= 2) {
                    int x = Integer.parseInt(coordinates[0].trim());
                    int y = Integer.parseInt(coordinates[1].trim());
                    
                    // Find the cell with these coordinates
                    Cell pathCell = null;
                    for (Cell cell : cells) {
                        if(cell.getRow() == x && cell.getCol() == y) {
                            cell.setPath(true);
                            pathCell = cell;
                            break;
                        }
                    }
                    
                    if(pathCell != null) {
                        pathCells.add(pathCell);
                    } else {
                        // Create a new cell if not found (this should be rare/never happen if cells are initialized properly)
                        Cell newCell = new Cell(x, y, 70, true);
                        pathCells.add(newCell);
                        cells.add(newCell);
                    }
                }
            }
        }
        return pathCells;
    }
    
    public int getLevelWidth() {
        return Integer.parseInt((rows[0].split(":"))[1]);
    }

    public int getLevelHeight() {
        return Integer.parseInt((rows[1].split(":"))[1]);
    }
}
