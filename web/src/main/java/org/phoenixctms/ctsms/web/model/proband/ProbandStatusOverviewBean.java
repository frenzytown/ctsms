package org.phoenixctms.ctsms.web.model.proband;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.phoenixctms.ctsms.vo.ProbandStatusEntryOutVO;
import org.phoenixctms.ctsms.web.component.datatable.DataTable;
import org.phoenixctms.ctsms.web.model.ManagedBeanBase;
import org.phoenixctms.ctsms.web.model.shared.CollidingInventoryBookingEagerModel;
import org.phoenixctms.ctsms.web.util.DefaultSettings;
import org.phoenixctms.ctsms.web.util.SettingCodes;
import org.phoenixctms.ctsms.web.util.Settings;
import org.phoenixctms.ctsms.web.util.Settings.Bundle;
import org.phoenixctms.ctsms.web.util.WebUtil;

@ManagedBean
@ViewScoped
public class ProbandStatusOverviewBean extends ManagedBeanBase {

	private static final boolean COLLIDING_VISIT_SCHEDULE_ITEMS_ALL_PROBAND_GROUPS = false;
	private static final Boolean COLLIDING_VISIT_SCHEDULE_ITEMS_INTERNAL = false;
	private ProbandStatusLazyModel probandStatusModel;
	private HashMap<Long, CollidingInventoryBookingEagerModel> collidingInventoryBookingModelCache;
	private HashMap<Long, CollidingVisitScheduleItemEagerModel> collidingVisitScheduleItemModelCache;
	private boolean showCollisions;

	public ProbandStatusOverviewBean() {
		super();
		collidingInventoryBookingModelCache = new HashMap<Long, CollidingInventoryBookingEagerModel>();
		collidingVisitScheduleItemModelCache = new HashMap<Long, CollidingVisitScheduleItemEagerModel>();
		showCollisions = Settings.getBoolean(SettingCodes.PROBAND_STATUS_OVERVIEW_SHOW_COLLISIONS_PRESET, Bundle.SETTINGS,
				DefaultSettings.PROBAND_STATUS_OVERVIEW_SHOW_COLLISIONS_PRESET);
		probandStatusModel = new ProbandStatusLazyModel();
	}

	public CollidingInventoryBookingEagerModel getCollidingInventoryBookingModel(ProbandStatusEntryOutVO statusEntry) {
		return CollidingInventoryBookingEagerModel.getCachedCollidingInventoryBookingModel(statusEntry, showCollisions, collidingInventoryBookingModelCache);
	}

	public CollidingVisitScheduleItemEagerModel getCollidingVisitScheduleItemModel(ProbandStatusEntryOutVO statusEntry) {
		return CollidingVisitScheduleItemEagerModel.getCachedCollidingVisitScheduleItemModel(statusEntry, showCollisions, COLLIDING_VISIT_SCHEDULE_ITEMS_ALL_PROBAND_GROUPS,
				COLLIDING_VISIT_SCHEDULE_ITEMS_INTERNAL,
				collidingVisitScheduleItemModelCache);
	}

	public ProbandStatusLazyModel getProbandStatusModel() {
		return probandStatusModel;
	}

	public void handleIgnoreObsoleteChange() {
		initSets();
	}

	@PostConstruct
	private void init() {
		initIn();
		initSets();
	}

	private void initIn() {
	}

	private void initSets() {
		collidingInventoryBookingModelCache.clear();
		collidingVisitScheduleItemModelCache.clear();
		probandStatusModel.updateRowCount();
		DataTable.clearFilters("probandstatus_list");
	}

	@Override
	public boolean isCreateable() {
		return false;
	}

	@Override
	public boolean isCreated() {
		return false;
	}

	@Override
	public String loadAction() {
		initIn();
		initSets();
		return LOAD_OUTCOME;
	}

	public String probandStatusToColor(ProbandStatusEntryOutVO statusEntry) {
		if (statusEntry != null) {
			if (!statusEntry.getType().getProbandActive()) {
				return WebUtil.colorToStyleClass(Settings.getColor(SettingCodes.NA_STATUS_COLOR, Bundle.SETTINGS, DefaultSettings.NA_STATUS_COLOR));
			}
		}
		return "";
	}

	public void handleShowCollisionsChange() {
		collidingInventoryBookingModelCache.clear();
		collidingVisitScheduleItemModelCache.clear();
	}

	public boolean isShowCollisions() {
		return showCollisions;
	}

	public void setShowCollisions(boolean showCollisions) {
		this.showCollisions = showCollisions;
	}
}
