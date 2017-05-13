package io.guaong.a2048;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Block extends FrameLayout {

    private TextView blockNumText;
    private int num;

    public Block(Context context){
        super(context);
        initBlock();
    }

    private void initBlock(){
        blockNumText = new TextView(getContext());
        blockNumText.setTextSize(32);
        blockNumText.setTextColor(0xffffffff);
        blockNumText.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.setMargins(15, 15, 0, 0);
        addView(blockNumText, layoutParams);
        setNum(0);
    }

    public void setNum(int num){
        this.num = num;
        switch (num){
            case 0:setBackground(io.guaong.a2048.Color.ZERO, "");break;
            case 2:setBackground(io.guaong.a2048.Color.ONE, num+"");break;
            case 4:setBackground(io.guaong.a2048.Color.TWO, num+"");break;
            case 8:setBackground(io.guaong.a2048.Color.THREE, num+"");break;
            case 16:setBackground(io.guaong.a2048.Color.FOUR, num+"");break;
            case 32:setBackground(io.guaong.a2048.Color.FIVE, num+"");break;
            case 64:setBackground(io.guaong.a2048.Color.SIX, num+"");break;
            case 128:setBackground(io.guaong.a2048.Color.SEVEN, num+"");break;
            case 256:setBackground(io.guaong.a2048.Color.EIGHT, num+"");break;
            case 512:setBackground(io.guaong.a2048.Color.NINE, num+"");break;
            case 1024:setBackground(io.guaong.a2048.Color.TEN, num+"");break;
            case 2048:setBackground(io.guaong.a2048.Color.ELEVEN, num+"");break;
            case 4096:setBackground(io.guaong.a2048.Color.TWELVE, num+"");break;
            case 8192:setBackground(io.guaong.a2048.Color.THIRTEEN, num+"");break;
            case 16384:setBackground(io.guaong.a2048.Color.FOURTEEN, num+"");break;
            case 32768:setBackground(io.guaong.a2048.Color.FIFTEEN, num+"");break;
            case 65536:setBackground(io.guaong.a2048.Color.SIXTEEN, num+"");break;
            default:setBackground(io.guaong.a2048.Color.OTHER, num+"");break;
        }
    }

    private void setBackground(int color, String str){
        blockNumText.setText(str);
        blockNumText.setBackgroundColor(color);
    }

    public int getNum(){
        return num;
    }

}
