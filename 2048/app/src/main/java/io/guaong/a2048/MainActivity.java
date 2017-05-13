package io.guaong.a2048;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Environment;
import android.os.Process;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * 程序名:2048
 * @author 关桐
 * @version 1.0
 * 该类主要实现了2048的操作
 * 开始时间 2017/4/28
 * 完成时间 2017/5/2 20:15
 *
 */
public class MainActivity extends AppCompatActivity {

    private BlockLayout blockLayout;
    private TextView gradeText;
    private TextView historyGradeText;
    private Button againBtn;
    //用于记录最高成绩的txt文件
    private File gradeTxt;
    private Timer timer;
    //4*4矩阵
    private Block[][] blocks = new Block[4][4];
    //用于存放空白Block
    private List<Point> emptyBlocks = new ArrayList<>();
    //用于记录当前存在Block数量
    private int haveBlockCount;
    //用于记录成绩
    private int grade;
    //记录历史成绩
    private int historyGrade;
    boolean isFirstBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cancelActionBar();
        getId();
        againBtn.setOnTouchListener(new ButtonTouchListener());
        initBlockLayout();
        createGradeTxt();
        setHistoryGrade();
    }

    @Override
    public void onBackPressed() {
        timer = new Timer();
        if (isFirstBack){
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            isFirstBack = false;
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    isFirstBack = true;
                }
            };
            timer.schedule(timerTask, 2000);
        }else {
            finish();
            System.exit(0);
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 取消actionBar
     */
    private void cancelActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * 获取控件id
     */
    private void getId(){
        blockLayout = (BlockLayout) this.findViewById(R.id.blockLayout);
        gradeText = (TextView) findViewById(R.id.textView);
        historyGradeText = (TextView) findViewById(R.id.textView2);
        againBtn = (Button) findViewById(R.id.button);
    }

    /**
     * 初始化界面
     */
    private void initBlockLayout() {
        setBlockLayout();
        addBlocks(getBlockSize().x, getBlockSize().y);
        blockLayout.setOnTouchListener(new MoveListener());
        newGame();
    }

    /**
     * 设置BlockLayout布局
     */
    private void setBlockLayout(){
        ViewGroup.LayoutParams layoutParams = blockLayout.getLayoutParams();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        layoutParams.height = layoutParams.width = metric.widthPixels - 20;
        blockLayout.setLayoutParams(layoutParams);
        blockLayout.setColumnCount(4);
    }

    /**
     * 获取Block尺寸
     * @return Point类型，用于记录Block的w,h
     */
    private Point getBlockSize(){
        int blockWidth, blockHeight;
        blockHeight = blockWidth =
                (Math.min(blockLayout.getLayoutParams().height, blockLayout.getLayoutParams().width) - 15) / 4;
        return new Point(blockWidth, blockHeight);
    }

    /**
     * 将Block添加到BlockLayout中
     * @param blockWidth Block的宽度
     * @param blockHeight Block的高度
     */
    private void addBlocks(int blockWidth, int blockHeight) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                blocks[x][y] = new Block(this);
                blocks[x][y].setNum(0);
                blockLayout.addView(blocks[x][y], blockWidth, blockHeight);
            }
        }
    }

    /**
     * 开始游戏
     */
    private void newGame() {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                blocks[x][y].setNum(0);
            }
        }
        randomBlock();
        randomBlock();
        gradeText.setText("0");
        haveBlockCount = 2;
        grade = 0;
    }

    /**
     * 随机产生新Block
     */
    private void randomBlock() {
        emptyBlocks.clear();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (blocks[x][y].getNum() == 0) {
                    emptyBlocks.add(new Point(x, y));
                }
            }
        }
        Point point = emptyBlocks.remove((int) (Math.random() * emptyBlocks.size()));
        blocks[point.x][point.y].setNum(randomNum());

    }

    /**
     * 随机产生Block上数字
     * @return 2或4 概率9:1
     */
    private int randomNum() {
        return Math.random() > 0.1 ? 2 : 4;
    }

    /**
     * 创建用于记录成绩的txt文件
     * @return 创建成功或者失败
     */
    private boolean createGradeTxt() {
        String path = "grade.txt";
        gradeTxt = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + path);
        try {
            return gradeTxt.exists() && gradeTxt.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将历史成绩添加到TextView控件
     */
    private void setHistoryGrade() {
        historyGrade = readGradeTxt(gradeTxt);
        String string = "历史成绩:" + historyGrade;
        historyGradeText.setText(string);
    }

    /**
     * 读取历史成绩txt文件
     * @param file 一个存在的txt文件
     * @return 成绩 将file中读取的string转化为int
     */
    private int readGradeTxt(File file) {
        String str;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            if ((str = bufferedReader.readLine()) != null) {
                bufferedReader.close();
                return Integer.parseInt(str);
            } else {
                bufferedReader.close();
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 写入最高成绩
     * @param file 一个存在的txt文件
     * @param grade 最高成绩
     */
    private void writeGradeTxt(File file, int grade) {
        try {
            Writer writer = new FileWriter(file);
            writer.write(grade + "");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 手势监听
     */
    class MoveListener implements View.OnTouchListener {

        /**
         * 常量用于表示手势方向
         */
        final int MOVE_TO_LEFT = 1;
        final int MOVE_TO_RIGHT = 2;
        final int MOVE_TO_UP = 3;
        final int MOVE_TO_DOWN = 4;

        //按下时位置
        private float downX;
        private float downY;
        //偏移位置
        private float offsetX;
        private float offsetY;

        //判断是否可以移动
        private boolean canNotMove;
        //判断是否可以合并,其实是再次合并的意思，指当合并一次后不能再次合并
        boolean canUnion[][] = new boolean[4][4];

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            canNotMove = true;
            initCanUnion();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    down(event);
                    break;
                case MotionEvent.ACTION_UP:
                    up(event);
                    break;
                default:
                    break;
            }
            //在执行一次手势之后再次随机产生一个Block
            if (!canNotMove) {
                shouldRandomBlocks();
                canNotMove = true;
            }
            initCanUnion();
            //设置成绩
            updateGrade();
            //修改最高分
            if (historyGrade < grade) {
                updateHistoryGrade();
            }
            //判断失败
            if (haveBlockCount > 15){
                if (isGameOver()) {
                    gameOver();
                }
            }
            return true;
        }

        /**
         * 初始化判断能否合并数组
         */
        private void initCanUnion() {
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    canUnion[x][y] = true;
                }
            }
        }

        /**
         * 判断游戏失败
         * @return 失败或者没有失败
         */
        private boolean isGameOver(){
            int count = 0;
            for (int x = 0; x < 4; x++) {
                for (int y = 1; y < 4; y++) {
                    if (blocks[x][y].getNum() != blocks[x][y - 1].getNum()) {
                        count++;
                    }
                }
            }
            for (int y = 0; y < 4; y++) {
                for (int x = 1; x < 4; x++) {
                    if (blocks[x][y].getNum() != blocks[x - 1][y].getNum()) {
                        count++;
                    }
                }
            }
            return count == 24;
        }

        /**
         * 游戏结束
         */
        private void gameOver(){
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setMessage("你输了");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new ButtonClickListener());
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        /**
         * 更新历史成绩
         */
        private void updateHistoryGrade(){
            writeGradeTxt(gradeTxt, grade);
            String string = "历史成绩:" + grade;
            historyGradeText.setText(string);
        }

        /**
         * 更新成绩
         */
        private void updateGrade(){
            String gradeStr = "" + grade;
            gradeText.setText(gradeStr);
        }

        /**
         * 按下时记录x,y
         * @param event 传递MotionEvent对象
         */
        private void down(MotionEvent event) {
            downX = event.getX();
            downY = event.getY();
        }

        /**
         * 计算抬起时位置的偏移量
         * @param event 传递MotionEvent对象
         */
        private void up(MotionEvent event) {
            offsetX = event.getX() - downX;
            offsetY = event.getY() - downY;
            moveAction(moveDirection(offsetX, offsetY));
        }

        /**
         * 获取手势滑动方向
         * @param offsetX 传递偏移x
         * @param offsetY 传递偏移y
         * @return 移动的方向
         */
        private int moveDirection(float offsetX, float offsetY) {
            boolean isCorrectTouch =
                    (Math.abs(offsetX / offsetY) >= 2 || Math.abs(offsetX / offsetY) < 0.5)
                            && (Math.abs(offsetX) > 20 || Math.abs(offsetY) > 20);
            //判断是否是合法的方向，ag.方向为东北即为非法
            if (isCorrectTouch) {
                boolean isHorizontalMove = Math.abs(offsetX) > Math.abs(offsetY);
                //判断是向上下还是左右
                if (isHorizontalMove) {
                    boolean isToLeft = offsetX < 0;
                    if (isToLeft) {
                        return MOVE_TO_LEFT;
                    } else {
                        return MOVE_TO_RIGHT;
                    }
                } else {
                    boolean isToUp = offsetY < 0;
                    if (isToUp) {
                        return MOVE_TO_UP;
                    } else {
                        return MOVE_TO_DOWN;
                    }
                }
            } else {
                return -1;
            }
        }

        /**
         * 应该产生随机Block,当当前BlockLayout中有16块以下时可以产生Block
         */
        private void shouldRandomBlocks() {
            if (haveBlockCount <= 15) {
                randomBlock();
                haveBlockCount++;
            }
        }

        /**
         * 手势所对应事件
         * @param direction 方向
         */
        private void moveAction(int direction) {
            switch (direction) {
                case MOVE_TO_DOWN:
                    toDown();
                    break;
                case MOVE_TO_UP:
                    toUp();
                    break;
                case MOVE_TO_LEFT:
                    toLeft();
                    break;
                case MOVE_TO_RIGHT:
                    toRight();
                    break;
                default:
                    break;
            }
        }

        /**
         * 遍历从第1列到最后一列所有12个Block(从第0列开始)
         * 每一个Block向前遍历,找到相同另一Block,首先判断当前Block是否可以合并,
         *      若可以将另一Block数字乘2,Block置为0,
         *      同时haveBlockCount减一,
         *      grade累加,
         *      另一Block的canUnion设为false,
         *      canNotMove设为false
         * 如果在向前遍历时发现有另一非零Block与Block不相等,直接跳出循环
         * 如果在向前遍历时发现有另一Block为0,将Block值赋给另一Block,Block置为0,
         *      canNotMove设为false
         */
        private void toLeft() {
            for (int y = 0; y < 4; y++) {
                for (int x = 1; x < 4; x++) {
                    int flagX = x;
                    for (int i = x - 1; i >= 0; i--) {
                        if (blocks[i][y].getNum() == 0 && blocks[flagX][y].getNum() != 0) {
                            blocks[i][y].setNum(blocks[flagX][y].getNum());
                            blocks[flagX][y].setNum(0);
                            flagX = i;
                            canNotMove = false;
                        } else if (blocks[i][y].getNum() == blocks[flagX][y].getNum() && blocks[i][y].getNum() != 0) {
                            if (canUnion[i][y]) {
                                blocks[i][y].setNum(blocks[i][y].getNum() * 2);
                                haveBlockCount--;
                                grade += blocks[i][y].getNum();
                                blocks[flagX][y].setNum(0);
                                canUnion[i][y] = false;
                                canNotMove = false;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        private void toRight() {
            for (int y = 0; y < 4; y++) {
                for (int x = 2; x >= 0; x--) {
                    int flagX = x;
                    for (int i = x + 1; i <= 3; i++) {
                        if (blocks[i][y].getNum() == 0 && blocks[flagX][y].getNum() != 0) {
                            blocks[i][y].setNum(blocks[flagX][y].getNum());
                            blocks[flagX][y].setNum(0);
                            flagX = i;
                            canNotMove = false;
                        } else if (blocks[i][y].getNum() == blocks[flagX][y].getNum() && blocks[i][y].getNum() != 0) {
                            if (canUnion[i][y]) {
                                blocks[i][y].setNum(blocks[i][y].getNum() * 2);
                                haveBlockCount--;
                                grade += blocks[i][y].getNum();
                                blocks[flagX][y].setNum(0);
                                canUnion[i][y] = false;
                                canNotMove = false;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        private void toUp() {
            for (int x = 0; x < 4; x++) {
                for (int y = 1; y < 4; y++) {
                    int flagY = y;
                    for (int i = y - 1; i >= 0; i--) {
                        if (blocks[x][i].getNum() == 0 && blocks[x][flagY].getNum() != 0) {
                            blocks[x][i].setNum(blocks[x][flagY].getNum());
                            blocks[x][flagY].setNum(0);
                            flagY = i;
                            canNotMove = false;
                        } else if (blocks[x][i].getNum() == blocks[x][flagY].getNum() && blocks[x][i].getNum() != 0) {
                            if (canUnion[x][i]) {
                                blocks[x][i].setNum(blocks[x][i].getNum() * 2);
                                haveBlockCount--;
                                grade += blocks[x][i].getNum();
                                blocks[x][flagY].setNum(0);
                                canUnion[x][i] = false;
                                canNotMove = false;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }

        }

        private void toDown() {
            for (int x = 0; x < 4; x++) {
                for (int y = 2; y >= 0; y--) {
                    int flagY = y;
                    for (int i = y + 1; i <= 3; i++) {
                        if (blocks[x][i].getNum() == 0 && blocks[x][flagY].getNum() != 0) {
                            blocks[x][i].setNum(blocks[x][flagY].getNum());
                            blocks[x][flagY].setNum(0);
                            flagY = i;
                            canNotMove = false;
                        } else if (blocks[x][i].getNum() == blocks[x][flagY].getNum() && blocks[x][i].getNum() != 0) {
                            if (canUnion[x][i]) {
                                blocks[x][i].setNum(blocks[x][i].getNum() * 2);
                                haveBlockCount--;
                                grade += blocks[x][i].getNum();
                                blocks[x][flagY].setNum(0);
                                canUnion[x][i] = false;
                                canNotMove = false;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

    }

    /**
     * 重开按钮监听
     */
    class ButtonTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    againBtn.setBackgroundResource(R.drawable.btn_bg);
                    break;
                case MotionEvent.ACTION_DOWN:
                    newGame();
                    againBtn.setBackgroundResource(R.drawable.btn_bg_down);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    /**
     * 弹窗按钮监听
     */
    class ButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (DialogInterface.BUTTON_POSITIVE == which){
                newGame();
            }

        }
    }


}
