package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.zalando.zomcat.jobs.JobsStatusBean;
import de.zalando.zomcat.jobs.markup.helper.HoverOddEvenElement;
import de.zalando.zomcat.jobs.model.JobGroupRow;
import de.zalando.zomcat.jobs.model.JobGroupRowModel;
import de.zalando.zomcat.jobs.model.JobMonitorForm;
import de.zalando.zomcat.jobs.model.JobMonitorModel;
import de.zalando.zomcat.jobs.model.JobRow;
import de.zalando.zomcat.jobs.model.JobRowsHistoryModel;
import de.zalando.zomcat.jobs.model.JobRowsModel;

public class JobMonitorFragment extends Form<JobMonitorForm> {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    private final JobRowsModel jobSelections = new JobRowsModel();
    private final JobGroupRowModel jobGroupRowModel = new JobGroupRowModel();
    private final JobRowsHistoryModel jobRowsHistoryModel = new JobRowsHistoryModel();

    public JobMonitorFragment(final String id, final JobMonitorModel jobMonitorModel) {
        super(id, jobMonitorModel);

        setOutputMarkupPlaceholderTag(true);

        final CheckGroup<JobRow> group = new CheckGroup<JobRow>("group", jobSelections);

        group.add(new CheckGroupSelector("groupselector"));
        add(group);

        final ListView<JobGroupRow> listview = new ListView<JobGroupRow>("jobGroupRow", jobGroupRowModel) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(final ListItem<JobGroupRow> item) {
                    final JobGroupRow jobGroupRow = item.getModelObject();

                    final AjaxSubmitLink groupLink = new AjaxSubmitLink("toggleGroup") {
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                            jobGroupRowModel.toggleVisible(jobGroupRow.getGroupName());
                            target.add(form);
                        }

                        @Override
                        protected void onError(final AjaxRequestTarget target, final Form<?> form) { }
                    };

                    groupLink.add(new Label("jobGroupName", jobGroupRow.getGroupName()));

                    item.add(groupLink);
                    item.add(new JobGroupModeFragment(jobGroupRowModel, item.getModelObject()));

                    final JobRowsModel jobRowsModel = new JobRowsModel(jobGroupRow.getGroupName(), jobRowsHistoryModel);

                    item.add(new ListView<JobRow>("jobDetailRow", jobRowsModel) {
                            private static final long serialVersionUID = 1L;

                            @Override
                            protected Item<JobRow> newItem(final int index, final IModel<JobRow> model) {
                                final ListItem<JobRow> newItem = super.newItem(index, model);
                                return new HoverOddEvenElement(newItem.getId(), index, model);
                            }

                            @Override
                            protected void populateItem(final ListItem<JobRow> item) {
                                item.add(new JobFragment(item.getModel(), jobRowsModel));
                            }

                            @Override
                            protected void onConfigure() {
                                setVisibilityAllowed(jobGroupRow.isVisible());
                            }

                        }.setReuseItems(false));
                }
            }.setReuseItems(false);

        // encapsulate the ListView in a WebMarkupContainer in order for it
        // to update
        final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        listContainer.setOutputMarkupId(true);
        listContainer.add(listview);

        group.add(new AjaxSubmitLink("enableSelected", this) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    if (target != null) {
                        for (final JobRow jobRow : group.getModelObject()) {
                            jobsStatusBean.getJobTypeStatusBean(jobRow.getJobClass()).setDisabled(false);
                        }

                        target.add(listContainer);
                    }
                }

                @Override
                protected void onError(final AjaxRequestTarget target, final Form<?> form) { }
            });

        group.add(new AjaxSubmitLink("disableSelected", this) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    if (target != null) {
                        for (final JobRow jobTypeStatusBean : group.getModelObject()) {
                            jobsStatusBean.getJobTypeStatusBean(jobTypeStatusBean.getJobClass()).setDisabled(true);
                        }

                        target.add(listContainer);
                    }
                }

                @Override
                protected void onError(final AjaxRequestTarget target, final Form<?> form) { }
            });

        group.add(listContainer);
    }
}
