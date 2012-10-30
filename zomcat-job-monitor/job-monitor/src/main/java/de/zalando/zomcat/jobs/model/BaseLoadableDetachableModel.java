package de.zalando.zomcat.jobs.model;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class BaseLoadableDetachableModel<T> extends LoadableDetachableModel<T> {
    private static final long serialVersionUID = 1L;

    public BaseLoadableDetachableModel() {
        Injector.get().inject(this);
    }

    public BaseLoadableDetachableModel(final T a_entity) {
        super(a_entity);
        Injector.get().inject(this);
    }
}
