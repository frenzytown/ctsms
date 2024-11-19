package org.phoenixctms.ctsms.web.model.proband;

import java.util.Date;

import org.phoenixctms.ctsms.exception.AuthenticationException;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.util.CommonUtil;
import org.phoenixctms.ctsms.vo.VisitScheduleItemOutVO;
import org.phoenixctms.ctsms.web.util.DefaultSettings;
import org.phoenixctms.ctsms.web.util.MessageCodes;
import org.phoenixctms.ctsms.web.util.Messages;
import org.phoenixctms.ctsms.web.util.SettingCodes;
import org.phoenixctms.ctsms.web.util.Settings;
import org.phoenixctms.ctsms.web.util.Settings.Bundle;
import org.phoenixctms.ctsms.web.util.WebUtil;

public class AddVisitScheduleItemReimbursementBean extends AddReimbursementBeanBase {

	private VisitScheduleItemOutVO visitScheduleItem;
	private float visitReimbursementAmount;
	private int visitScheduleItemCount;
	private final static boolean ALIQUOT_VISIT_REIMBURSEMENT = false;

	public AddVisitScheduleItemReimbursementBean() {
		super();
	}

	@Override
	protected String changeAction(Long id) {
		out = null;
		this.trialId = null;
		this.visitScheduleItem = WebUtil.getVisitScheduleItem(id);
		if (this.visitScheduleItem != null) {
			this.trialId = visitScheduleItem.getTrial() != null ? visitScheduleItem.getTrial().getId() : null;
		}
		initIn();
		initSets(true);
		return CHANGE_OUTCOME;
	}

	@Override
	protected String getComment() {
		StringBuilder comment = new StringBuilder();
		if (visitScheduleItem != null) {
			appendTravelExpenseComment(comment, visitScheduleItem.getStart(), getReimbursementTitle());
			appendTicketExpenseComment(comment, visitScheduleItem.getStart(), getReimbursementTitle());
			appendAccommodationExpenseComment(comment, visitScheduleItem.getStart(), visitScheduleItem.getStop());
			appendReimbursementComment(comment, visitScheduleItem.getStart(), getReimbursementTitle());
		}
		return comment.toString();
	}

	@Override
	protected String getReimbursementTitle() {
		if (visitScheduleItem != null) {
			String reimbursementTitle;
			if (visitScheduleItem.getVisit() != null) {
				if (!CommonUtil.isEmptyString(visitScheduleItem.getToken())) {
					reimbursementTitle = Messages.getMessage(MessageCodes.ADD_REIMBURSEMENT_COMMENT_TOKEN_VISIT_LABEL, visitScheduleItem.getVisit().getTitle(),
							visitScheduleItem.getToken(),
							visitScheduleItemCount);
				} else {
					reimbursementTitle = Messages.getMessage(MessageCodes.ADD_REIMBURSEMENT_COMMENT_VISIT_LABEL, visitScheduleItem.getVisit().getTitle(), visitScheduleItemCount);
				}
			} else {
				reimbursementTitle = Messages.getMessage(MessageCodes.ADD_REIMBURSEMENT_COMMENT_VISIT_SCHEDULE_ITEM_LABEL, visitScheduleItem.getName(), visitScheduleItemCount);
			}
			return CommonUtil.clipString(reimbursementTitle,
					Settings.getInt(SettingCodes.ADD_REIMBURSEMENT_COMMENT_CLIP_MAX_LENGTH, Bundle.SETTINGS, DefaultSettings.ADD_REIMBURSEMENT_COMMENT_CLIP_MAX_LENGTH));
		}
		return null;
	}

	public VisitScheduleItemOutVO getVisitScheduleItem() {
		return visitScheduleItem;
	}

	public void setStart(Date start) {
		if (visitScheduleItem != null) {
			visitScheduleItem.setStart(start);
		}
	}

	public void setStop(Date stop) {
		if (visitScheduleItem != null) {
			visitScheduleItem.setStop(stop);
		}
	}

	@Override
	protected void initSpecificSets(boolean reset) {
		super.initSpecificSets(reset);
		if (reset) {
			visitReimbursementAmount = 0.0f;
			reimbursementAmount = 0.0f;
			if (visitScheduleItem != null && visitScheduleItem.getVisit() != null) {
				visitReimbursementAmount = visitScheduleItem.getVisit().getReimbursement();
				reimbursementAmount = visitReimbursementAmount;
			}
			visitScheduleItemCount = 1;
			if (ALIQUOT_VISIT_REIMBURSEMENT && probandId != null && visitScheduleItem != null) {
				try {
					visitScheduleItemCount = CommonUtil.safeLongToInt(WebUtil
							.getServiceLocator()
							.getTrialService()
							.getVisitScheduleItemCount(WebUtil.getAuthentication(), visitScheduleItem.getTrial().getId(),
									visitScheduleItem.getGroup() != null ? visitScheduleItem.getGroup().getId() : null,
									visitScheduleItem.getVisit() != null ? visitScheduleItem.getVisit().getId() : null, probandId, false, true));
				} catch (ServiceException | AuthorisationException | IllegalArgumentException e) {
				} catch (AuthenticationException e) {
					WebUtil.publishException(e);
				}
			}
			if (visitScheduleItemCount > 1) {
				reimbursementAmount = reimbursementAmount / (visitScheduleItemCount);
			}
			addReimbursement = reimbursementAmount > 0.0f;
		}
	}

	@Override
	public boolean isCreateable() {
		if (in.getProbandId() == null || visitScheduleItem == null) {
			return false;
		} else {
			return !WebUtil.isTrialLocked(visitScheduleItem.getTrial()) && !WebUtil.isProbandLocked(proband) && WebUtil.isProbandPerson(proband);
		}
	}
}
