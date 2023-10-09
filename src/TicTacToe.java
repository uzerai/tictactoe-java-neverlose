import java.util.*;

public class TicTacToe {
    public ArrayList<Integer> availableSquares = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
    // Score represents the score of a player across row1, row2, row3, col1, col2, col3, diag1, diag2
    private final float[] score = new float[] {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private final ArrayList<Integer> humanPlayer = new ArrayList<>();
    private final ArrayList<Integer> computerPlayer = new ArrayList<>();

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        boolean isPlayer1Turn = true;

        while(!game.hasWinner()) {
            game.drawBoard();

            if (isPlayer1Turn) {
                if (game.playerTurn(game.humanPlayer)) {
                    isPlayer1Turn = false;
                }
            } else {
                game.computerTurn(game.computerPlayer);
                isPlayer1Turn = true;
            }
        }

        game.drawBoard();
    }

    public boolean hasWinner() {
        if (availableSquares.isEmpty()) {
            System.out.println("#####################");
            System.out.println("##### TIE GAME ######");
            System.out.println("### NO MORE SPACE ###");
            System.out.println("#####################");
            return true;
        }

        for (float v : score) {
            if (v == 3.0) {
                System.out.println("#####################");
                System.out.println("#### HUMAN  WINS ####");
                System.out.println("#####################");
                return true;
            } else if (v == -3.0) {
                System.out.println("#####################");
                System.out.println("### COMPUTER WINS ###");
                System.out.println("#####################");
                return true;
            }
        }

        return false;
    }

    private void computerTurn(ArrayList<Integer> player) {
        // The score array represents the score per row/row/row, col/col/col, diag/diag
        // so let's make sure we can find out where the opponent is winning from the value
        // of the score key. ie: if score[0] > 0f, we deduce they must be attempting to
        // win on column of squares [ 1, 2, 3 ] (score[6] and score[3] will also be > 0f)
        Map<Integer, Integer[]> scoreToSquaresMap = Map.of(
                0, new Integer[]{1,2,3},
                1, new Integer[]{4,5,6},
                2, new Integer[]{7,8,9},
                3, new Integer[]{1,4,7},
                4, new Integer[]{2,5,8},
                5, new Integer[]{3,6,9},
                6, new Integer[]{1,5,9},
                7, new Integer[]{3,5,7});
        List<Integer> blockingPrioritySquares = new ArrayList<>();
        List<Integer> winningPrioritySquares = new ArrayList<>();
        List<Integer> cornerSquares = new ArrayList<>(List.of(1, 3, 7, 9));

        int index = 0;
        for (float f: score) {
            if (f > 1f) {
                blockingPrioritySquares.addAll(List.of(scoreToSquaresMap.get(index)));
            } else if (f < 0f) {
                winningPrioritySquares.addAll(List.of(scoreToSquaresMap.get(index)));
            }
            index++;
        }

        blockingPrioritySquares.retainAll(availableSquares);
        winningPrioritySquares.retainAll(availableSquares);
        cornerSquares.retainAll(availableSquares);
        // Prioritize corner squares over middle squares, done by just sorting the winning prio list
        // and putting all possible corner squares in front (as we sort priority closer to element 0).
        winningPrioritySquares.sort((a, b) -> cornerSquares.contains(a) ? -1 : a - b);

        Integer square = null;
        if(blockingPrioritySquares.isEmpty() && availableSquares.size() > 7) {
            // Middle square is most important, always in the start.
            if (availableSquares.contains(5)) {
                square = 5;
            } else {
                // if not available, pick random corner square
                square = cornerSquares.get(new Random().nextInt(0, cornerSquares.size() - 1));
            }
        } else if (blockingPrioritySquares.isEmpty() && !winningPrioritySquares.isEmpty()) {
            square = winningPrioritySquares.get(0);
        } else {
            square = blockingPrioritySquares.get(0);
        }

        System.out.printf("COMPUTER plays in square ('O'):%n%d%n", square);

        recordMove(square, player, false);
    }

    private boolean playerTurn(ArrayList<Integer> player) {
        System.out.println("Select a square to place your mark ('X'):");
        Scanner consoleIn = new Scanner(System.in);
        if (consoleIn.hasNextInt()) {
            int square = consoleIn.nextInt();

            return recordMove(square, player, true);
        }
        consoleIn.close();

        // assume it fails otherwise
        return false;
    }

    private boolean recordMove(int square, ArrayList<Integer> player, boolean isHumanPlayer) {
        if (!availableSquares.contains(square)) {
            System.out.println("Cannot select this square; Select another square.");
            return false;
        } else {
            // purely for representation of the board.
            player.add(square);
            setScore(square, isHumanPlayer);
            availableSquares.remove(Integer.valueOf(square));
            return true;
        }
    }

    private void setScore(int square, boolean isHumanPlayer) {
        // point increment is -1 if player2, 1 if player1
        float pointIncrement = isHumanPlayer ? 1f : -1f;

        switch(square) {
            case 1:
                this.score[0] += pointIncrement;
                this.score[3] += pointIncrement;
                this.score[6] += pointIncrement;
                break;
            case 2:
                this.score[0] += pointIncrement;
                this.score[4] += pointIncrement;
                break;
            case 3:
                this.score[0] += pointIncrement;
                this.score[5] += pointIncrement;
                this.score[7] += pointIncrement;
                break;
            case 4:
                this.score[1] += pointIncrement;
                this.score[3] += pointIncrement;
                break;
            case 5:
                this.score[1] += pointIncrement;
                this.score[4] += pointIncrement;
                this.score[6] += pointIncrement;
                this.score[7] += pointIncrement;
                break;
            case 6:
                this.score[1] += pointIncrement;
                this.score[5] += pointIncrement;
                break;
            case 7:
                this.score[2] += pointIncrement;
                this.score[3] += pointIncrement;
                this.score[7] += pointIncrement;
                break;
            case 8:
                this.score[2] += pointIncrement;
                this.score[4] += pointIncrement;
                break;
            case 9:
                this.score[2] += pointIncrement;
                this.score[5] += pointIncrement;
                this.score[6] += pointIncrement;
                break;
            default:
                break;
        }

    }

    private void drawBoard() {
//        System.out.printf("SCORE IS %s%n%n", Arrays.toString(score));
        System.out.println("=== Current board: ===");
        System.out.printf("\t| %s | %s | %s |%n", drawSquare(1), drawSquare(2), drawSquare(3));
        System.out.println("\t-------------");
        System.out.printf("\t| %s | %s | %s |%n", drawSquare(4), drawSquare(5), drawSquare(6));
        System.out.println("\t-------------");
        System.out.printf("\t| %s | %s | %s |%n", drawSquare(7), drawSquare(8), drawSquare(9));
        System.out.println("=======================");
    }

    private String drawSquare(int square) {
        if (humanPlayer.contains(square)) {
            return "X";
        }

        if (computerPlayer.contains(square)) {
            return "O";
        }

        return String.valueOf(square);
    }
}