import java.lang.StringBuilder;
import java.util.*;
import java.io.*;

/**
 * @author      Firstname Lastname <address @ example.com>
 */
public class Board{

    // FIELD
    public final int GRID_SIZE;

    private char[][] grid;          // String Representation of Pac-man Game Board
    private boolean[][] visited;    // Record of where Pac-man has visited
    private PacCharacter pacman;    // Pac-man that user controls
    private PacCharacter[] ghosts;  // 4 Ghosts that controlled by the program
    private int score;              // Score Recorded for the gamer


    /*
     * Constructor
     *
     * <p> Description: TODO
     *
     * @param:  TODO
     * @return: TODO
     */
    public Board(int size) {

        // Initialize instance variables
        GRID_SIZE = size;
        grid = new char[GRID_SIZE][GRID_SIZE];
        visited = new boolean[GRID_SIZE][GRID_SIZE];
        score = 0;

        pacman = new PacCharacter(GRID_SIZE/2, GRID_SIZE/2, 'P');
        ghosts = new PacCharacter[4];
        ghosts[0] = new PacCharacter(          0,           0, 'G');
        ghosts[1] = new PacCharacter(          0, GRID_SIZE-1, 'G');
        ghosts[2] = new PacCharacter(GRID_SIZE-1,           0, 'G');
        ghosts[3] = new PacCharacter(GRID_SIZE-1, GRID_SIZE-1, 'G');

        setVisited(GRID_SIZE/2, GRID_SIZE/2);

        refreshGrid();
    }



    // To Tutors: this is for PA6
    public Board(String inputBoard) throws IOException {
        // Create a scanner to scan the inputBoard.
        Scanner input = new Scanner(new File(inputBoard));

        // First integer in inputBoard is GRID_SIZE.
        GRID_SIZE = input.nextInt();
        // Second integer in inputBoard is score.
        score = input.nextInt();

        grid = new char[GRID_SIZE][GRID_SIZE];
        visited = new boolean[GRID_SIZE][GRID_SIZE];
        String line = input.nextLine(); // Skip current line (score line)

        char rep;
        ghosts = new PacCharacter[4];
        for ( int rowIndex = 0; rowIndex < GRID_SIZE; rowIndex++ )
        {
            line = input.nextLine();
            for ( int colIndex = 0; colIndex < GRID_SIZE; colIndex++ ) {
                rep = line.charAt(colIndex);
                grid[rowIndex][colIndex] = rep;

                switch (rep) {
                    case 'P':
                        setVisited(rowIndex, colIndex);
                        pacman = new PacCharacter(rowIndex, colIndex, 'P');
                        break;
                    case 'G':
                        for (int i = 0; i < ghosts.length; i++) {
                            if (ghosts[i] == null) {
                                ghosts[i] = new PacCharacter(rowIndex, colIndex, 'G');;
                                break;
                            }
                        }
                        break;
                    case 'X':
                        pacman = new PacCharacter(rowIndex, colIndex, 'P');
                        for (int i = 0; i < ghosts.length; i++) {
                            if (ghosts[i] == null) {
                                ghosts[i] = new PacCharacter(rowIndex, colIndex, 'G');;
                                break;
                            }
                        }
                        break;
                    case ' ':
                        setVisited(rowIndex, colIndex);
                        break;
                }
            }
        }


    }


    public int getScore() {
        return score;
    }


    public char[][] getGrid() {
        return grid;
    }

    public void setVisited(int x, int y) {
        if (x < 0 || y < 0 || x >= GRID_SIZE || y > GRID_SIZE) return;
        visited[x][y] = true;
    }

    public void refreshGrid() {

        for (int i = 0; i < GRID_SIZE; i++)
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!visited[i][j])
                    grid[i][j] = '*';
                else
                    grid[i][j] = ' ';
            }

        grid[pacman.getRow()][pacman.getCol()] = pacman.getAppearance();
        for (PacCharacter ghost : ghosts) {
            if (pacman.getRow() == ghost.getRow() && pacman.getCol() == ghost.getCol())
                grid[ghost.getRow()][ghost.getCol()] = 'X';
            else grid[ghost.getRow()][ghost.getCol()] = ghost.getAppearance();
        }

    }


    public boolean canMove(Direction direction) {
        if (direction == null) return false;
        // Calculate Coordinate after Displacement
        int pacmanRow = pacman.getRow() + direction.getY();
        int pacmanCol = pacman.getCol() + direction.getX();

        return pacmanRow >= 0 && pacmanRow < GRID_SIZE && pacmanCol >= 0 && pacmanCol < GRID_SIZE;
    }


    public void move(Direction direction) {
        // Calculate Coordinate after Displacement
        int pacmanRow = pacman.getRow() + direction.getY();
        int pacmanCol = pacman.getCol() + direction.getX();

        pacman.setPosition(pacmanRow, pacmanCol);
        if (!visited[pacmanRow][pacmanCol]) {
            score += 10;
            visited[pacmanRow][pacmanCol] = true;
        }

        for (PacCharacter ghost : ghosts) {
            ghostMove(ghost);
        }

        refreshGrid();
    }


    public boolean isGameOver() {
        int pacmanRow = pacman.getRow();
        int pacmanCol = pacman.getCol();

        for (PacCharacter ghost : ghosts)
            if (ghost.getRow() == pacmanRow && ghost.getCol() == pacmanCol)
                return true;

        return false;

    }

    // Monster always move towards Pac-man
    public Direction ghostMove(PacCharacter ghost) {
        int pacmanRow = pacman.getRow();
        int pacmanCol = pacman.getCol();

        int ghostRow = ghost.getRow();
        int ghostCol = ghost.getCol();

        int rowDist = Math.abs(ghostRow - pacmanRow);
        int colDist = Math.abs(ghostCol - pacmanCol);

        if (rowDist == 0 && colDist > 0) {
            if (ghostCol - pacmanCol > 0) {
                ghost.setPosition(ghostRow, ghostCol - 1);
                return Direction.LEFT;
            } else { // ghostCol - pacmanCol < 0
                ghost.setPosition(ghostRow, ghostCol + 1);
                return Direction.RIGHT;
            }
        }
        else if (rowDist > 0 && colDist == 0 ) {
            if (ghostRow - pacmanRow > 0) {
                ghost.setPosition(ghostRow - 1, ghostCol);
                return Direction.UP;
            } else { // ghostRow - pacmanRow < 0
                ghost.setPosition(ghostRow + 1, ghostCol);
                return Direction.DOWN;
            }
        }
        else if (rowDist == 0 && colDist == 0) {
            return Direction.STAY;
        }
        else {
            if (rowDist < colDist) {
                if (ghostRow - pacmanRow > 0) {
                    ghost.setPosition(ghostRow - 1, ghostCol);
                    return Direction.UP;
                } else { // ghostRow - pacmanRow < 0
                    ghost.setPosition(ghostRow + 1, ghostCol);
                    return Direction.DOWN;
                }
            } else {
                if (ghostCol - pacmanCol > 0) {
                    ghost.setPosition(ghostRow, ghostCol - 1);
                    return Direction.LEFT;
                } else { // ghostCol - pacmanCol < 0
                    ghost.setPosition(ghostRow, ghostCol + 1);
                    return Direction.RIGHT;
                }
            }
        }

    }




    // To Tutors: this is for PA6
    public void saveBoard(String outputBoard) throws IOException
    {
        PrintWriter output = new PrintWriter(new File(outputBoard));
        // First print out the GRID_SIZE.
        output.println(GRID_SIZE);
        // Second print out the score.
        output.println(score);

        for ( int rowIndex = 0; rowIndex < GRID_SIZE; rowIndex++ )
        {
            for ( int colIndex = 0; colIndex < GRID_SIZE; colIndex++ )
                output.print(grid[rowIndex][colIndex]);
            output.print("\n");
        }
        output.close();
    }



    public String toString(){

        StringBuilder outputString = new StringBuilder();
        outputString.append(String.format("Score: %d\n", this.score));

        for (int row = 0; row < GRID_SIZE; row++)
        {
            for (int column = 0; column < GRID_SIZE; column++) {
                outputString.append("  ");
                outputString.append(grid[row][column]);
            }

            outputString.append("\n");
        }
        return outputString.toString();

    }
