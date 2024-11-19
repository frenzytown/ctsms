package org.phoenixctms.ctsms.web.model.staff;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.phoenixctms.ctsms.vo.StaffStatusEntryOutVO;
import org.phoenixctms.ctsms.web.component.datatable.DataTable;
import org.phoenixctms.ctsms.web.model.ManagedBeanBase;
import org.phoenixctms.ctsms.web.model.proband.CollidingVisitScheduleItemEagerModel;
import org.phoenixctms.ctsms.web.model.shared.CollidingDutyRosterTurnEagerModel;
import org.phoenixctms.ctsms.web.model.shared.CollidingInventoryBookingEagerModel;
import org.phoenixctms.ctsms.web.util.DefaultSettings;
import org.phoenixctms.ctsms.web.util.SettingCodes;
import org.phoenixctms.ctsms.web.util.Settings;
import org.phoenixctms.ctsms.web.util.Settings.Bundle;
import org.phoenixctms.ctsms.web.util.WebUtil;

@ManagedBean
@ViewScoped
public class StaffStatusOverviewBean extends ManagedBeanBase {

	private StaffStatusLazyModel staffStatusModel;
	private HashMap<Long, CollidingDutyRosterTurnEagerModel> collidingDutyRosterTurnModelCache;
	private HashMap<Long, CollidingInventoryBookingEagerModel> collidingInventoryBookingModelCache;
	private HashMap<Long, CollidingVisitScheduleItemEagerModel> collidingVisitScheduleItemModelCache;
	private boolean showCollisions;

	public StaffStatusOverviewBean() {
		super();
		collidingDutyRosterTurnModelCache = new HashMap<Long, CollidingDutyRosterTurnEagerModel>();
		collidingInventoryBookingModelCache = new HashMap<Long, CollidingInventoryBookingEagerModel>();
		collidingVisitScheduleItemModelCache = new HashMap<Long, CollidingVisitScheduleItemEagerModel>();
		showCollisions = Settings.getBoolean(SettingCodes.STAFF_STATUS_OVERVIEW_SHOW_COLLISIONS_PRESET, Bundle.SETTINGS,
				DefaultSettings.STAFF_STATUS_OVERVIEW_SHOW_COLLISIONS_PRESET);
		staffStatusModel = new StaffStatusLazyModel();
	}

	public CollidingDutyRosterTurnEagerModel getCollidingDutyRosterTurnModel(StaffStatusEntryOutVO statusEntry) {
		return CollidingDutyRosterTurnEagerModel.getCachedCollidingDutyRosterTurnModel(statusEntry, showCollisions, collidingDutyRosterTurnModelCache);
	}

	public CollidingInventoryBookingEagerModel getCollidingInventoryBookingModel(StaffStatusEntryOutVO statusEntry) {
		return CollidingInventoryBookingEagerModel.getCachedCollidingInventoryBookingModel(statusEntry, showCollisions, collidingInventoryBookingModelCache);
	}

	public CollidingVisitScheduleItemEagerModel getCollidingVisitScheduleItemModel(StaffStatusEntryOutVO statusEntry) {
		return CollidingVisitScheduleItemEagerModel.getCachedCollidingVisitScheduleItemModel(statusEntry, showCollisions, collidingVisitScheduleItemModelCache);
	}

	public StaffStatusLazyModel getStaffStatusModel() {
		return staffStatusModel;
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
		collidingDutyRosterTurnModelCache.clear();
		collidingInventoryBookingModelCache.clear();
		collidingVisitScheduleItemModelCache.clear();
		staffStatusModel.updateRowCount();
		DataTable.clearFilters("staffstatus_list");
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

	public String staffStatusToColor(StaffStatusEntryOutVO statusEntry) {
		if (statusEntry != null) {
			if (!statusEntry.getType().getStaffActive()) {
				return WebUtil.colorToStyleClass(Settings.getColor(SettingCodes.NA_STATUS_COLOR, Bundle.SETTINGS, DefaultSettings.NA_STATUS_COLOR));
			}
		}
		return "";
	}

	public void handleShowCollisionsChange() {
		collidingDutyRosterTurnModelCache.clear();
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
