package tk.com.sharemusic.myview;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * 自定义TagHandler使其能够识别font 中的size设置字体大小
 */
public class MyHtmlTagHandler implements Html.TagHandler {
    private String tagName;
    private int startIndex = 0;
    private int endIndex = 0;

    final HashMap<String,String> attributes = new HashMap<>();

    public MyHtmlTagHandler(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        //判断是否是当前需要的tag
        if (tag.equalsIgnoreCase(tagName)){
            //解析所有属性值
            paraseAttributes(xmlReader);
            if (opening){
                startHandleTag(tag,output,xmlReader);
            }else {
                endEndHandleTag(tag,output,xmlReader);
            }
        }
    }

    private void endEndHandleTag(String tag, Editable output, XMLReader xmlReader) {
        endIndex = output.length();

        //获取属性值
        String color = attributes.get("color");
        String size = attributes.get("size");

        //设置字体大小
        if (!TextUtils.isEmpty(size)){
            size = size.split("px")[0];
            output.setSpan(new AbsoluteSizeSpan(Integer.parseInt(size)),startIndex,endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //设置字体颜色
        if (!TextUtils.isEmpty(color)){
            output.setSpan(new ForegroundColorSpan(Color.parseColor(color)),startIndex,endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void startHandleTag(String tag, Editable output, XMLReader xmlReader) {
        startIndex = output.length();
    }

    /**
     * 解析所有属性值
     * @param xmlReader
     */
    private void paraseAttributes(XMLReader xmlReader) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            if (element==null)
                return;
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (int) lengthField.get(atts);
            for (int i=0;i<len;i++){
                attributes.put(data[i*5+1],data[i*5+4]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
