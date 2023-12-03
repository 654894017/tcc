package com.damon.tcc.event_source;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class Source {
    private final List<Event> events = new ArrayList<>();

    protected void applyNewEvent(Event event) {
        apply(event);
        events.add(event);
    }

    public abstract Integer getVersion();

    @SuppressWarnings("deprecation")
    private void apply(Event event) {
        try {
            Method method = this.getClass().getDeclaredMethod("apply", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 回滚到上一个版本的状态
     */
    public void resetLastVersion() {
        List<Event> copyEvents = new ArrayList<>(events);
        events.clear();
        Integer lastVersion = getVersion();
        copyEvents.stream().filter(event -> event.getVersion() < lastVersion).forEach(event -> {
            applyNewEvent(event);
        });
    }

}
