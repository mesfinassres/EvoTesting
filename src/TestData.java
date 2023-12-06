import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;

public class TestData {

    private String srcURL, srcHtmlShape, trigger, dstURL, dstHtmlShape;
    public TestData (String srcURL, String srcHtmlShape, String trigger, String dstURL, String dstHtmlShape){
        this.setDstURL(srcURL);
        this.setSrcHtmlShape(srcHtmlShape);
        this.setTrigger(trigger);
        this.setDstURL(dstURL);
        this.setDstHtmlShape(dstHtmlShape);
    }
    private void setSrcURL(String newSrcURL){
     this.srcURL = newSrcURL;
    }
    private void setSrcHtmlShape(String newSrcHtmlShape){
        this.srcHtmlShape = newSrcHtmlShape;
    }
    private void setTrigger(String newTrigger){
        this.trigger = newTrigger;
    }
    private void setDstURL(String newDstURL){
        this.dstURL = newDstURL;
    }
    private void setDstHtmlShape(String newDstHtmlShape) {
        this.dstHtmlShape = newDstHtmlShape;
    }
    private String getSrcURL(){
        return srcURL;
    }
    private String getSrcHtmlShape(){
        return srcHtmlShape;
    }
    private String getTrigger(){
        return trigger;
    }
    private String getDstURL(){
        return dstURL;
    }
    private String getDstHtmlShape() {
        return dstHtmlShape;
    }

    public JSONObject buildJsonObject(){

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("srcURL", this.getSrcURL());
        jsonObject.put("srcHtmlShape", this.getSrcHtmlShape());
        jsonObject.put("Trigger", this.getTrigger());
        jsonObject.put("dstURL", this.getDstURL());
        jsonObject.put("dstHtmlShape", this.getDstHtmlShape());

        return jsonObject;
    }
}