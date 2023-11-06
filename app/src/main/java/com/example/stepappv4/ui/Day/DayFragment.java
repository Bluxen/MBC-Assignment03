package com.example.stepappv4.ui.Day;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentDayBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DayFragment extends Fragment {
    public int todaySteps = 0;
    public int[] weekSteps = new int[7];
    TextView numStepsTextView;
    AnyChartView anyChartView;

    Date cDate = new Date();
    String current_date = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

    public Map<String, Integer> stepsByDay = null;
    private FragmentDayBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Create column chart
        anyChartView = root.findViewById(R.id.dayBarChart);
        anyChartView.setProgressBar(root.findViewById(R.id.dayLoadingBar));

        Cartesian cartesian = createColumnChart();
        anyChartView.setBackgroundColor("#00000000");
        anyChartView.setChart(cartesian);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public Cartesian createColumnChart() {
        //***** Read data from SQLiteDatabase *********/
        Map<String, Integer> graph_map = new TreeMap<>();
        //go back 6 days
        Calendar c = Calendar.getInstance();
        c.setTime(cDate);
        c.add(Calendar.DATE, -6);
        cDate = c.getTime();
        String current_day = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        //put the steps of the day and of the previous 6 in the map
        for (int i = 0; i<7; i++) {
            graph_map.put(current_day, StepAppOpenHelper.loadSingleRecord(getContext(), current_day));
            c.setTime(cDate);
            c.add(Calendar.DATE, 1);
            cDate = c.getTime();
            current_day = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        }

        //***** Create column chart using AnyChart library *********/
        // TODO 4: Create and get the cartesian coordinate system for column chart
        Cartesian cartesian = AnyChart.column();

        // TODO 5: Create data entries for x and y axis of the graph
        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        // TODO 6: Add the data to column chart and get the columns
        Column column = cartesian.column(data);

        //***** Modify the UI of the chart *********/
        // TODO 7 (YOUR TURN): Change the color of column chart and its border
        column.fill("#1EB980");
        column.stroke("#1EB980");

        // TODO 8: Modifying properties of tooltip
        column.tooltip()
                .titleFormat("At day: {%X}")
                .format("{%Value} Steps")
                .anchor(Anchor.RIGHT_BOTTOM);

        // TODO 9 (YOUR TURN): Modify column chart tooltip properties
        column.tooltip()
                .position(Position.RIGHT_TOP)
                .offsetX(0d)
                .offsetY(5);

        // Modifying properties of cartesian
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.yScale().minimum(0);


        // TODO 10 (YOUR TURN): Modify the UI of the cartesian
        cartesian.xAxis(0).title("Day");
        cartesian.yAxis(0).title("Steps");
        cartesian.background().fill("#00000000");
        cartesian.animation(true);

        return cartesian;
    }
}