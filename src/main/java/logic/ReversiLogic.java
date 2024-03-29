package logic;

import java.util.ArrayList;

/**
 * РEВЕРСИ
 *
 * В Реверси принимают участие два игрока. Один играет черными фигурами, второй – белыми. Фигуры именуются бочонками.
 *
 * Делая ход, игрок должен поставить свой бочонок между уже находящейся на доске фишкой
 * своего цвета и непрерывной линией (горизонтальной, вертикальной или по диагонали) бочонков соперника, как бы закрывая
 * фишки соперника с двух сторон. Количество перекрытых фишек соперника не важно - чем больше, тем лучше. При этом можно
 * за один ход закрывать несколько таких линий.
 *
 * Правила игры:
 * 1) Вначале компьютер определяет автоматическим методом жребия, кому из участников достаются черные и белые бочонки;
 * 2) Далее игроки выставляют свои четыре фишки на начальные позиции;
 * 3) Далее первый ход по правилам принадлежит участнику, которому достались черные фигуры;
 * 4) Всего используемых фишек 64, особенность этой игры состоит в том что фишки меняют цвет, изначально 4 фишки
 *    находятся на поле (2 черных и две белых) - остальные 60 фишек находятся за пределами поля и выставляются
 *    поочередно;
 * 5) Сделать ход на поле означает взятой вне поля фишкой закрыть проход сопернику, выставляя ее таким образом, чтобы
 *    между одной вашей фишкой и другой (выставленной) образовался туннель, заполненный бочонками противника (за один
 *    ход можно выставить на поле только один бочонок);
 * 6) Все закрытые (побитые) бочонки с поля боя забирает ходивший игрок, т.е. меняет фишки противника на свои; При этом
 *    меняются бочонки соперника находящиеся между вашими бочонками как по прямой, так по диагонали. Количество
 *    меняющихся бочонков не ограничено, так же как и количество закрытых линий;
 * 7) Если у игроков есть возможность ходить фишками, то есть не все ходы перекрыты соперником, то отказаться они от
 *    своего хода не имеют права;
 * 8) Если все ходы перекрыты, то участник пропускает ход;
 * 9) Бой продолжается до того, когда все бочонки противника будут закрыты.
 *
 * Окончание игры знаменуется невозможностью противников сделать ход. При этом высчитывается количество фигур
 * участников, которые занимают определенные позиции на поле боя. Чьих фишек больше, тот и является выигравшим.
 * В ситуации, когда количество позиционированных фигур на поле боя одинаково, объявляется ничья.
 */
public class ReversiLogic {
    // Переменные требуемые, для реализации реверси
    private final int rows = 8, columns = 8; // Число строк и колонок
    private int userStep = 0; // Состояние шага игрока (-1 - шаг невозможен)
    private int computerStep = 0; // Состояние шага компьютера (-1 - шаг невозможен)
    public Cell[][] field; // Реализация игрового поля

    // Переменные требуемые, для реализации искусственного интеллекта
    private static final int AILevel = 4; // Глубина работы искусственного интеллекта
    private static int minMaxX, minMaxY; // Координаты наиболее оптимального шага компьютера, полученные при минимаксе

    public ReversiLogic() {
        int mid = rows / 2; // Координата середины игрового поля

        // Инициализация игровой доски
        field = new Cell[8][8];
        for (int i = 0; i < rows; i++) {
            field[i] = new Cell[8];
        }

        // Инициализация клеток доски в начале игры
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                char c = (char) (97 + j);
                if ((i == mid - 1) && (j == mid - 1)) {
                    field[i][j] = new Cell(c, i + 1, 'X');
                } else if ((i == mid - 1) && (j == mid)) {
                    field[i][j] = new Cell(c, i + 1, 'O');
                } else if ((i == mid) && (j == mid - 1)) {
                    field[i][j] = new Cell(c, i + 1, 'O');
                } else if ((i == mid) && (j == mid)) {
                    field[i][j] = new Cell(c, i + 1, 'X');
                } else field[i][j] = new Cell(c, i + 1, '.');
            }
        }
    }

    // Поиск возможных вариантов хода
    public void findLegalMove(char turn, ArrayList <Integer> arr) {
        int[] numberOfMoves = new int[1]; // Число перевернутых бочонков при определенном шаге
        char enemy; // Имя "врага", относительно которого будут вычисляться возможные шаги
        if (turn == 'O') enemy = 'X';
        else enemy = 'O';

        // Рассматриваются только пустые клетки и рассчитывается возможность перекрытия бочонков соперника
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (field[i][j].getStatus() == '.') {
                    move(i, j, false, turn, enemy, numberOfMoves);
                    if (numberOfMoves[0] != 0) {
                        arr.add(i);
                        arr.add(j);
                    }
                }
            }
        }
    }

    // Ход компьютера
    public void play() {
        int maxProfit = 0; // Максимальное число перекрытых бочонков
        int[] numberOfMoves = new int[1]; // Число перевернутых бочонков при определенном шаге

        // Поиск хода с максимально возможным количеством перекрытий бочонков
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (field[i][j].getStatus() == '.') {
                    move(i, j, false, 'O', 'X', numberOfMoves);
                    if (numberOfMoves[0] > maxProfit) maxProfit = numberOfMoves[0];
                }
            }
        }

        // Если хода нет, он переходит к игроку
        computerStep = maxProfit;
        if (computerStep == 0) computerStep = -1;
        else {
            runMinMax(0, 'O', 0);
            move(minMaxX, minMaxY, true, 'O', 'X', numberOfMoves);
        }
    }

    // Минимакс с альфа-бета отсечением (находит наиболее оптимальный шаг для компьютера)
    private int runMinMax(int depth, char turn, int score) {
        // Возвращает оценку текущей ветви ходов, если глубина алгоритма достигает уровня искусственного интеллекта
        if (depth == AILevel) return score;

        // Возвращает оценку текущей ветви ходов в случае выигрыша (поле заполнено бочонками)
        if (checkOccupancy() == 1) {
            if (endOfGame() == 1 || endOfGame() == 3) return 10000000;
            else if (endOfGame() == 2) return -10000000;
        }

        // Инициализация кучи переменных
        ArrayList<Integer> moves = new ArrayList<>(); // Массив с координатами доступных шагов для игрока turn
        findLegalMove(turn, moves); // Получение координат
        int[] numberOfMoves = new int[1]; // Число перевернутых бочонков при определенном шаге
        int bestMove = Integer.MIN_VALUE; // Оценка лучшего шага
        int beta = Integer.MAX_VALUE; // Более благоприятная оценка для минимизирующего игрока
        int alpha = Integer.MIN_VALUE; // Более благоприятная оценка для максимизирующего игрока

        // Дополнительное поле, которе используется для моделирования отдельной ветки
        Cell[][] fieldLocal = new Cell[8][8];
        for (int i = 0; i < rows; i++) {
            fieldLocal[i] = new Cell[8];
            for (int j = 0; j < columns; j++) {
                char c = (char) (97 + j);
                fieldLocal[i][j] = new Cell(c, i + 1, field[i][j].getStatus());
            }
        }

        if (moves.isEmpty()) {
            return 0;
        } else {
            // Обработка всех возможных щагов
            for (int i = 0; i < moves.size() / 2; i += 2) {
                int scoreLocal = score;
                int cellX = moves.get(i);
                int cellY = moves.get(i + 1);

                // На шаге 'O' происходит максимизация оценки возможного шага
                if (turn == 'O') {
                    // Моделируется шаг компьютера
                    move(cellX, cellY, true, 'O', 'X', numberOfMoves);

                    // Оценка шага весами разных зон игрового поля относительно хода компьютера (зоны обозначены в файле "images/fieldValues.png")
                    if ((cellX == 0 || cellX == 7) && (cellY == 0 || cellY == 7)) {
                        scoreLocal += 40; // Зона №1
                    }
                    if ((cellX > 1 && cellX < 6) && (cellY == 0 || cellY == 7)) {
                        scoreLocal += 20; // Зона №2
                    }
                    if ((cellY > 1 && cellY < 6) && (cellX == 0 || cellX == 7)) {
                        scoreLocal += 20; // Зона №2
                    }
                    if (cellX > 1 && cellX < 6 && cellY > 1 && cellY < 6) {
                        scoreLocal += 5; // Зона №3
                    }
                    if ((cellX > 1 && cellX < 6) && (cellY == 1 || cellY == 6)) {
                        scoreLocal -= 20; // Зона №4
                    }
                    if ((cellY > 1 && cellY < 6) && (cellX == 1 || cellX == 6)) {
                        scoreLocal -= 20; // Зона №4
                    }
                    if ((cellX == 1 || cellX == 6) && (cellY < 2 || cellY > 5)) {
                        scoreLocal -= 40; // Зона №5
                    }
                    if ((cellY == 1 || cellY == 6) && (cellX < 2 || cellX > 5)) {
                        scoreLocal -= 40; // Зона №5
                    }

                    // Оценка текущего шага с последующей её передачей по ветви ходов
                    int currentScore = runMinMax(depth + 1, 'X', scoreLocal + numberOfMoves[0]);

                    // Вычисление альфы
                    if (currentScore > alpha) alpha = currentScore;

                    // Запись удовлетворяющих результатов
                    if (depth == 0 && currentScore > bestMove) {
                        bestMove = currentScore;
                        minMaxX = cellX;
                        minMaxY = cellY;
                    }

                    // Случай победы компьютера
                    if (currentScore >= 10000) {
                        for (int k = 0; k < 8; k++) {
                            for (int z = 0; z < 8; z++) {
                                field[k][z].setPosition(fieldLocal[k][z].getX(), fieldLocal[k][z].getY(), fieldLocal[k][z].getStatus());
                            }
                        }
                        return 1000000;
                    }

                    // Отсечения ветвей, которые заведомо нам не нравятся
                    if (beta <= alpha) {
                        for (int k = 0; k < 8; k++) {
                            for (int z = 0; z < 8; z++) {
                                field[k][z].setPosition(fieldLocal[k][z].getX(), fieldLocal[k][z].getY(), fieldLocal[k][z].getStatus());
                            }
                        }
                        break;
                    }

                // На шаге 'X' происходит минимизация оценки возможного шага
                } else if (turn == 'X') {
                    // Моделируется шаг игрока
                    move(cellX, cellY, true, 'X', 'O', numberOfMoves);

                    // Оценка шага весами разных зон игрового поля относительно хода игрока (зоны обозначены в файле "images/fieldValues.png")
                    if ((cellX == 0 || cellX == 7) && (cellY == 0 || cellY == 7)) {
                        scoreLocal -= 40; // Зона №1
                    }
                    if ((cellX > 1 && cellX < 6) && (cellY == 0 || cellY == 7)) {
                        scoreLocal -= 20; // Зона №2
                    }
                    if ((cellY > 1 && cellY < 6) && (cellX == 0 || cellX == 7)) {
                        scoreLocal -= 20; // Зона №2
                    }
                    if (cellX > 1 && cellX < 6 && cellY > 1 && cellY < 6) {
                        scoreLocal -= 5; // Зона №3
                    }
                    if ((cellX > 1 && cellX < 6) && (cellY == 1 || cellY == 6)) {
                        scoreLocal += 20; // Зона №4
                    }
                    if ((cellY > 1 && cellY < 6) && (cellX == 1 || cellX == 6)) {
                        scoreLocal += 20; // Зона №4
                    }
                    if ((cellX == 1 || cellX == 6) && (cellY < 2 || cellY > 5)) {
                        scoreLocal += 40; // Зона №5
                    }
                    if ((cellY == 1 || cellY == 6) && (cellX < 2 || cellX > 5)) {
                        scoreLocal += 40; // Зона №5
                    }

                    // Рекурсивная оценка ветви ходов
                    int currentScore = runMinMax(depth + 1, 'O', scoreLocal - numberOfMoves[0]);

                    // Вычисление альфы
                    if (currentScore < beta) beta = currentScore;

                    // Случай выигрыша игрока
                    if (currentScore <= -10000) {
                        for (int k = 0; k < 8; k++) {
                            for (int z = 0; z < 8; z++) {
                                field[k][z].setPosition(fieldLocal[k][z].getX(), fieldLocal[k][z].getY(), fieldLocal[k][z].getStatus());
                            }
                        }
                        return -1000000;
                    }

                    // Отсечения ветвей, которые заведомо нам не нравятся
                    if (beta <= alpha) {
                        for (int k = 0; k < 8; k++) {
                            for (int z = 0; z < 8; z++) {
                                field[k][z].setPosition(fieldLocal[k][z].getX(), fieldLocal[k][z].getY(), fieldLocal[k][z].getStatus());
                            }
                        }
                        break;
                    }
                }

                // Восстановление игрового поля после моделирования ходов
                for (int k = 0; k < 8; k++) {
                    for (int z = 0; z < 8; z++) {
                        field[k][z].setPosition(fieldLocal[k][z].getX(), fieldLocal[k][z].getY(), fieldLocal[k][z].getStatus());
                    }
                }
            }
        }

        return turn == 'O' ? alpha : beta;
    }

    // Ход игрока
    public int play(int xCor, int yCor) {
        int maxProfit = 0; // Максимальное количество возможных перекрытий бочонков
        int[] numberOfMoves = new int[1];

        // Поиск хода с максимально возможным количеством перекрытий бочонков
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (field[i][j].getStatus() == '.') {
                    move(i, j, false, 'X', 'O', numberOfMoves);
                    if (maxProfit < numberOfMoves[0]) maxProfit = numberOfMoves[0];
                }
            }
        }

        // Если хода нет, он переходит к компьютеру
        userStep = maxProfit;
        if (userStep == 0) {
            userStep = -1;
            return -1;
        } else {
            if (field[xCor][yCor].getStatus() != '.') return 1; // Неверный ход (выбрана занятая клетка доски)
            if (move(xCor, yCor, true, 'X', 'O', numberOfMoves) == -1) return 1; // Неверный ход (бочонки не были перекрыты)
        }

        return 0;
    }

    // Проверка на то, остались ли свободные клетки на игровом поле
    // (1 - заполнено бочонками, -1 - присутствуют пустые клетки)
    public int checkOccupancy() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (field[i][j].getStatus() == '.') {
                    return -1;
                }
            }
        }

        return 1;
    }

    // Обработка конца игры
    public int endOfGame() {
        int[] arr = new int[3];
        controlElements(arr);

        int black = arr[0];
        int white = arr[1];
        int empty = arr[2];

        if ((userStep == -1 && computerStep == -1) || empty == 0) {
            if (userStep == -1 && computerStep == -1) return 0; // Нет возможных ходов
            else {
                if (white > black) return 1; // Победа белых
                else if (black > white) return 2; // Победа черных
                else return 3; // Ничья
            }
        }

        return -1; // Игра не закончена
    }

    // Подсчет текущего состояния игровой доски
    public void controlElements(int[] arr) {
        int black = 0;
        int empty = 0;
        int white = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(field[i][j].getStatus() == 'X') black++;
                else if (field[i][j].getStatus() == 'O') white++;
                else if(field[i][j].getStatus() == '.') empty++;
            }
        }

        arr[0] = black;
        arr[1] = white;
        arr[2] = empty;
    }

    // Восстановление игровой доски до изначального состояния
    public void reset() {
        int mid = rows / 2;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                char c = (char) (97 + j);
                if((i == mid-1) && (j == mid-1)) field[i][j].setPosition(c, i + 1, 'X');
                else if((i == mid-1) && (j == mid)) field[i][j].setPosition(c, i + 1, 'O');
                else if((i == mid) && (j == mid-1)) field[i][j].setPosition(c, i + 1, 'O');
                else if((i == mid) && (j == mid)) field[i][j].setPosition(c, i + 1, 'X');
                else field[i][j].setPosition(c, i + 1, '.');
            }
        }
    }

    // Обработка хода (поиск перекрытых бочонков)
    public int move(int xStep, int yStep, boolean change, char player, char enemy, int[] numberOfMoves) {
        int processingCoordinate; // Координата обрабатываемой клетки
        int emptyFlag = 0; // Флаг, при котором достигнута пустая клетка
        int tunnelFlag = 0; // Флаг, при котором обрабатывается туннель (1 - туннель не образовался, 2 - туннель образовался)
        int status = -1; // Статус хода
        int xDiagonal, yDiagonal; // Координаты диагональных обрабатываемых клеток
        int tempAmount; // Количество перекрытых бочонков
        char x;
        int y;
        numberOfMoves[0] = 0;

        // Рассматриваются ячейки, расположенные снизу
        if ((xStep + 1 < rows) && (field[xStep + 1][yStep].getStatus() == enemy)) {
            processingCoordinate = xStep;

            while ((processingCoordinate < rows) && (emptyFlag != -1) && (tunnelFlag != 2)) {
                processingCoordinate++;
                if (processingCoordinate < rows) {
                    if (field[processingCoordinate][yStep].getStatus() == enemy) {
                        tunnelFlag = 1; // // Туннель, заполненный бочонками противника, не образовался
                    } else if (field[processingCoordinate][yStep].getStatus() == player) {
                        tunnelFlag = 2; // Образовался туннель, заполненный бочонками противника
                    } else emptyFlag = -1; // Достигнута свободная клетка
                }
            }

            // Вычисление количества перекрытых бочонков
            if (tunnelFlag == 2) {
                tempAmount = processingCoordinate - xStep - 1;
                numberOfMoves[0] += tempAmount;
            }

            // Изменение статуса бочонков
            if (tunnelFlag == 2 && change) {
                for (int i = xStep; i < processingCoordinate; ++i) {
                    x = field[i][yStep].getX();
                    y = field[i][yStep].getY();
                    field[i][yStep].setPosition(x, y, player);
                }
                status = 0;
            }

            // Обнуление флагов
            tunnelFlag = 0;
            emptyFlag = 0;
        }

        // Рассматриваются ячейки, расположенные сверху
        if ((xStep - 1 >= 0) && (field[xStep - 1][yStep].getStatus() == enemy)) {
            processingCoordinate = xStep;

            while ((processingCoordinate >= 0) && (emptyFlag != -1) && (tunnelFlag != 2)) {
                processingCoordinate--;
                if (processingCoordinate >= 0) {
                    if (field[processingCoordinate][yStep].getStatus() == enemy) {
                        tunnelFlag = 1; // // Туннель, заполненный бочонками противника, не образовался
                    } else if (field[processingCoordinate][yStep].getStatus() == player) {
                        tunnelFlag = 2; // Образовался туннель, заполненный бочонками противника
                    } else emptyFlag = -1; // Достигнута свободная клетка
                }
            }

            // Вычисление количества перекрытых бочонков
            if (tunnelFlag == 2) {
                tempAmount = xStep - processingCoordinate - 1;
                numberOfMoves[0] += tempAmount;
            }

            // Изменение статуса бочонков
            if (tunnelFlag == 2 && change) {
                for (int i = processingCoordinate; i <= xStep; ++i) {
                    x = field[i][yStep].getX();
                    y = field[i][yStep].getY();
                    field[i][yStep].setPosition(x, y, player);
                }
                status = 0;
            }

            // Обнуление флагов
            tunnelFlag = 0;
            emptyFlag = 0;
        }

        // Рассматриваются ячейки, расположенные справа
        if ((yStep + 1 < columns) && (field[xStep][yStep + 1].getStatus() == enemy)) {
            processingCoordinate = yStep;

            while ((processingCoordinate < columns) && (emptyFlag != -1) && (tunnelFlag != 2)) {
                processingCoordinate++;
                if (processingCoordinate < columns) {
                    if (field[xStep][processingCoordinate].getStatus() == enemy) {
                        tunnelFlag = 1; // // Туннель, заполненный бочонками противника, не образовался
                    } else if (field[xStep][processingCoordinate].getStatus() == player) {
                        tunnelFlag = 2; // Образовался туннель, заполненный бочонками противника
                    } else emptyFlag = -1; // Достигнута свободная клетка
                }
            }

            // Вычисление количества перекрытых бочонков
            if (tunnelFlag == 2) {
                tempAmount = processingCoordinate - yStep - 1;
                numberOfMoves[0] += tempAmount;
            }

            // Изменение статуса бочонков
            if(tunnelFlag == 2 && change) {
                for (int i = yStep; i < processingCoordinate; ++i) {
                    x = field[xStep][i].getX();
                    y = field[xStep][i].getY();
                    field[xStep][i].setPosition(x, y, player);
                }
                status = 0;
            }

            // Обнуление флагов
            tunnelFlag = 0;
            emptyFlag = 0;
        }

        // Рассматриваются ячейки, расположенные слева
        if ((yStep - 1 >= 0) && (field[xStep][yStep - 1].getStatus() == enemy)) {
            processingCoordinate = yStep;

            while ((processingCoordinate >= 0) && (emptyFlag != -1) && (tunnelFlag != 2)) {
                processingCoordinate--;
                if (processingCoordinate >= 0){
                    if (field[xStep][processingCoordinate].getStatus() == enemy) {
                        tunnelFlag = 1; // // Туннель, заполненный бочонками противника, не образовался
                    } else if (field[xStep][processingCoordinate].getStatus() == player) {
                        tunnelFlag = 2; // Образовался туннель, заполненный бочонками противника
                    } else emptyFlag = -1;
                }
            }

            // Вычисление количества перекрытых бочонков
            if (tunnelFlag == 2) {
                tempAmount = yStep - processingCoordinate - 1;
                numberOfMoves[0] += tempAmount;
            }

            // Изменение статуса бочонков
            if(tunnelFlag == 2 && change) {
                for (int i = processingCoordinate; i <= yStep; ++i) {
                    x = field[xStep][i].getX();
                    y = field[xStep][i].getY();
                    field[xStep][i].setPosition(x, y, player);
                }
                status = 0;
            }

            // Обнуление флагов
            tunnelFlag = 0;
            emptyFlag = 0;
        }

        // Рассматриваются ячейки, расположенные по диагонали справа-сверху
        if ((xStep - 1 >= 0) && (yStep + 1 < columns) && (field[xStep - 1][yStep + 1].getStatus() == enemy)) {
            yDiagonal = yStep;
            xDiagonal = xStep;

            while ((xDiagonal >= 0) && (yDiagonal < columns) && (emptyFlag != -1) && (tunnelFlag != 2)) {
                xDiagonal--;
                yDiagonal++;
                if ((xDiagonal >= 0) && (yDiagonal < columns)) {
                    if (field[xDiagonal][yDiagonal].getStatus() == enemy) {
                        tunnelFlag = 1;
                    } else if (field[xDiagonal][yDiagonal].getStatus() == player) {
                        tunnelFlag = 2; // Образовался туннель, заполненный бочонками противника
                    } else emptyFlag = -1; // Достигнута свободная клетка
                }
            }

            // Вычисление количества перекрытых бочонков
            if (tunnelFlag == 2) {
                tempAmount = xStep - xDiagonal - 1;
                numberOfMoves[0] += tempAmount;
            }

            // Изменение статуса бочонков
            if(tunnelFlag == 2 && change) {
                while((xDiagonal <= xStep) && (yStep < yDiagonal)) {
                    xDiagonal++;
                    yDiagonal--;
                    if((xDiagonal <= xStep) && (yStep <= yDiagonal)) {
                        x = field[xDiagonal][yDiagonal].getX();
                        y = field[xDiagonal][yDiagonal].getY();
                        field[xDiagonal][yDiagonal].setPosition(x, y, player);
                    }
                }
                status = 0;
            }

            // Обнуление флагов
            tunnelFlag = 0;
            emptyFlag = 0;
        }

        // Рассматриваются ячейки, расположенные по диагонали слева-сверху
        if ((xStep - 1 >= 0) && (yStep - 1 >= 0) && (field[xStep - 1][yStep - 1].getStatus() == enemy)) {
            yDiagonal = yStep;
            xDiagonal = xStep;

            while ((xDiagonal >= 0) && (yDiagonal >= 0) && (emptyFlag != -1) && (tunnelFlag != 2)) {
                xDiagonal--;
                yDiagonal--;
                if ((xDiagonal >= 0) && (yDiagonal >= 0)){
                    if (field[xDiagonal][yDiagonal].getStatus() == enemy) {
                        tunnelFlag = 1;
                    } else if(field[xDiagonal][yDiagonal].getStatus() == player) {
                        tunnelFlag = 2; // Образовался туннель, заполненный бочонками противника
                    } else emptyFlag = -1; // Достигнута свободная клетка
                }
            }

            // Вычисление количества перекрытых бочонков
            if (tunnelFlag == 2) {
                tempAmount = xStep - xDiagonal - 1;
                numberOfMoves[0] += tempAmount;
            }

            // Изменение статуса бочонков
            if(tunnelFlag == 2 && change) {
                while((xDiagonal <= xStep) && (yDiagonal <= yStep)) {
                    xDiagonal++;
                    yDiagonal++;
                    if((xDiagonal <= xStep) && (yDiagonal <= yStep)){
                        x = field[xDiagonal][yDiagonal].getX();
                        y = field[xDiagonal][yDiagonal].getY();
                        field[xDiagonal][yDiagonal].setPosition(x, y, player);
                    }
                }
                status = 0;
            }

            // Обнуление флагов
            tunnelFlag = 0;
            emptyFlag = 0;
        }

        // Рассматриваются ячейки, расположенные по диагонали справа-снизу
        if ((xStep + 1 < rows) && (yStep + 1 < columns) && (field[xStep + 1][yStep + 1].getStatus() == enemy)) {
            yDiagonal = yStep;
            xDiagonal = xStep;

            while ((xDiagonal < rows) && (yDiagonal < columns) && (emptyFlag != -1) && (tunnelFlag != 2)) {
                xDiagonal++;
                yDiagonal++;
                if ((xDiagonal < rows) && (yDiagonal < columns)) {
                    if (field[xDiagonal][yDiagonal].getStatus() == enemy) {
                        tunnelFlag = 1;
                    } else if (field[xDiagonal][yDiagonal].getStatus() == player) {
                        tunnelFlag = 2; // Образовался туннель, заполненный бочонками противника
                    } else emptyFlag = -1; // Достигнута свободная клетка
                }
            }

            // Вычисление количества перекрытых бочонков
            if (tunnelFlag == 2) {
                tempAmount = xDiagonal - xStep - 1;
                numberOfMoves[0] += tempAmount;
            }

            // Изменение статуса бочонков
            if(tunnelFlag == 2 && change) {
                while((xDiagonal >= xStep) && (yDiagonal >= yStep)) {
                    xDiagonal--;
                    yDiagonal--;
                    if((xDiagonal >= xStep) && (yDiagonal >= yStep)) {
                        x = field[xDiagonal][yDiagonal].getX();
                        y = field[xDiagonal][yDiagonal].getY();
                        field[xDiagonal][yDiagonal].setPosition(x, y, player);
                    }
                }
                status = 0;
            }

            // Обнуление флагов
            tunnelFlag = 0;
            emptyFlag = 0;
        }

        // Рассматриваются ячейки, расположенные по диагонали слева-снизу
        if ((xStep + 1 < rows) && (yStep - 1 >= 0) && (field[xStep + 1][yStep - 1].getStatus() == enemy)) {
            yDiagonal = yStep;
            xDiagonal = xStep;

            while ((xDiagonal < rows) && (yDiagonal >= 0) && (emptyFlag != -1) && (tunnelFlag != 2)) {
                xDiagonal++;
                yDiagonal--;
                if ((xDiagonal < rows) && (yDiagonal >= 0)) {
                    if (field[xDiagonal][yDiagonal].getStatus() == enemy) {
                        tunnelFlag = 1;
                    } else if (field[xDiagonal][yDiagonal].getStatus() == player) {
                        tunnelFlag = 2; // Образовался туннель, заполненный бочонками противника
                    } else emptyFlag = -1; // Достигнута свободная клетка
                }
            }

            // Вычисление количества перекрытых бочонков
            if (tunnelFlag == 2) {
                tempAmount = xDiagonal - xStep - 1;
                numberOfMoves[0] += tempAmount;
            }

            // Изменение статуса бочонков
            if(tunnelFlag == 2 && change) {
                while((xDiagonal >= xStep) && (yDiagonal <= yStep)) {
                    xDiagonal--;
                    yDiagonal++;
                    if((xDiagonal >= xStep) && (yDiagonal <= yStep)){
                        x = field[xDiagonal][yDiagonal].getX();
                        y = field[xDiagonal][yDiagonal].getY();
                        field[xDiagonal][yDiagonal].setPosition(x, y, player);
                    }
                }
                status = 0;
            }
        }

        return status;
    }
}