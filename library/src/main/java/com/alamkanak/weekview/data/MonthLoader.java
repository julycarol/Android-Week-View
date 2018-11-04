package com.alamkanak.weekview.data;

import com.alamkanak.weekview.ui.WeekView;
import com.alamkanak.weekview.model.WeekViewDisplayable;
import com.alamkanak.weekview.model.WeekViewEvent;
import com.alamkanak.weekview.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.alamkanak.weekview.utils.DateUtils.today;

/**
 * This class is responsible for loading {@link WeekViewEvent}s into {@link WeekView}. It can handle
 * both concrete {@link WeekViewEvent} objects and {@link WeekViewDisplayable} objects. The latter is
 * an interface that can be implemented in one's actual data class and handles the conversion to a
 * {@link WeekViewEvent}.
 */
public class MonthLoader implements WeekViewLoader {

    private MonthChangeListener onMonthChangeListener;

    public MonthLoader(MonthChangeListener listener){
        this.onMonthChangeListener = listener;
    }

    @Override
    public double toWeekViewPeriodIndex(Calendar instance){
        return instance.get(Calendar.YEAR) * 12
                + instance.get(Calendar.MONTH)
                + (instance.get(Calendar.DAY_OF_MONTH) - 1) / 30.0;
    }

    @Override
    public List<? extends WeekViewEvent> onLoad(int periodIndex) {
        final int year = periodIndex / 12;
        final int month = periodIndex % 12 + 1;

        final Calendar startDate = DateUtils.withTimeAtStartOfDay(today());
        startDate.set(Calendar.YEAR, year);
        startDate.set(Calendar.MONTH, month); // TODO: Test
        startDate.set(Calendar.DAY_OF_MONTH, 1);

        final int maxDays = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        final Calendar endDate = DateUtils.withTimeAtEndOfDay(today());
        startDate.set(Calendar.YEAR, year);
        startDate.set(Calendar.MONTH, month); // TODO: Test
        startDate.set(Calendar.DAY_OF_MONTH, maxDays);

        List<WeekViewDisplayable> displayableItems =
                onMonthChangeListener.onMonthChange(startDate, endDate);

        List<WeekViewEvent> events = new ArrayList<>();
        for (WeekViewDisplayable displayableItem : displayableItems) {
            events.add(displayableItem.toWeekViewEvent());
        }

        return events;
    }

    public MonthChangeListener getOnMonthChangeListener() {
        return onMonthChangeListener;
    }

    public void setOnMonthChangeListener(MonthChangeListener onMonthChangeListener) {
        this.onMonthChangeListener = onMonthChangeListener;
    }

    public interface MonthChangeListener {

        /**
         * Called when the month displayed in the {@link WeekView} changes.
         * @param startDate A {@link Calendar} representing the start date of the month
         * @param endDate A {@link Calendar} representing the end date of the month
         * @return The list of {@link WeekViewDisplayable} of the provided month
         */
        List<WeekViewDisplayable> onMonthChange(Calendar startDate, Calendar endDate);

    }
}
