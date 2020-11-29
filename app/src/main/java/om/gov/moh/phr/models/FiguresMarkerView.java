package om.gov.moh.phr.models;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import om.gov.moh.phr.R;


public class FiguresMarkerView extends MarkerView {
    private TextView tvContent;
    private String chartType;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     * @param barChart
     */
    public FiguresMarkerView(Context context, int layoutResource, String barChart) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.textView);
        this.chartType = barChart;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (chartType.equals("BarChart"))
            tvContent.setText(e.getData() + ", \n" + "+" + (int) e.getY());
        else
            tvContent.setText(e.getData() + ", \n" + (int) e.getY());
        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}
