package ca.lifesaver.engineers.it.vital.tracker;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class LineGraphFragment extends Fragment implements OnChartValueSelectedListener {
    private Handler handler;
    private LineChart lineChart;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private ArrayList<Entry> values;
    private Runnable updateRunnable;
    public LineGraphFragment() {
        // Required empty public constructor
    }

    public static LineGraphFragment newInstance() {
        return new LineGraphFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_graph, container, false);
        lineChart = view.findViewById(R.id.lineChart);
        setupLineChart();
        return view;
    }

    private void setupLineChart() {
        values = new ArrayList<>();
        lineDataSet = new LineDataSet(values, "Real Time Data");
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // Apply the additional settings as were previously in the VitalsFragment
        lineChart.setViewPortOffsets(0, 0, 0, 0);
        lineChart.setBackgroundColor(Color.rgb(104, 241, 175));
        lineChart.getDescription().setEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setMaxHighlightDistance(300);

        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x = lineChart.getXAxis();
        x.setEnabled(false);

        YAxis y = lineChart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateXY(2000, 2000);

        // Handle the chart value selected listener
        lineChart.setOnChartValueSelectedListener(this);


        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Generate random data
                LineData data = lineChart.getData();

                if (data != null) {
                    lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet.setCubicIntensity(0.2f);
                    lineDataSet.setDrawFilled(true);
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setLineWidth(1.8f);
                    lineDataSet.setCircleRadius(4f);
                    lineDataSet.setCircleColor(Color.WHITE);
                    lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                    lineDataSet.setColor(Color.WHITE);
                    lineDataSet.setFillColor(Color.WHITE);
                    lineDataSet.setFillAlpha(100);
                    lineDataSet.setDrawHorizontalHighlightIndicator(false);

                    lineDataSet.setFillFormatter(new IFillFormatter() {
                        @Override
                        public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                            return lineChart.getAxisLeft().getAxisMinimum();
                        }
                    });
                    ILineDataSet set = data.getDataSetByIndex(0);


                    if (set == null) {
                        set = createSet();
                        data.addDataSet(set);
                    }


                    // let the chart know it's data has changed
                    lineChart.notifyDataSetChanged();

                    // limit the number of visible entries
                    lineChart.setVisibleXRangeMaximum(10);
                    // chart.setVisibleYRange(30, AxisDependency.LEFT);

                    // move to the latest entry
                    lineChart.moveViewToX(data.getEntryCount());

                }




                // Schedule the next update
                handler.postDelayed(this, 2000);  // Update every 2 seconds
            }
        };

        handler.post(updateRunnable);

    }


    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    // You might need additional methods to manage data updates
    public void updateChartEntry(float heartRate) {
        if (lineDataSet != null && lineChart != null) {
            // Create a new Entry object for the chart
            Entry newEntry = new Entry(lineDataSet.getEntryCount(), heartRate);
            lineDataSet.addEntry(newEntry);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate(); // Refresh the chart
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove callbacks to avoid memory leaks
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
}