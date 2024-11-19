package org.phoenixctms.ctsms.web.model.shared;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.phoenixctms.ctsms.exception.AuthenticationException;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.js.JsUtil;
import org.phoenixctms.ctsms.util.CommonUtil;
import org.phoenixctms.ctsms.vo.InputFieldSelectionSetValueOutVO;
import org.phoenixctms.ctsms.vo.ProbandGroupOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueInVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueJsonVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValuesOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagsPDFVO;
import org.phoenixctms.ctsms.vo.UserOutVO;
import org.phoenixctms.ctsms.vo.VisitScheduleItemOutVO;
import org.phoenixctms.ctsms.web.model.ManagedBeanBase;
import org.phoenixctms.ctsms.web.model.Paginator;
import org.phoenixctms.ctsms.web.model.shared.inputfield.ProbandListEntryTagInputModel;
import org.phoenixctms.ctsms.web.model.shared.inputfield.ProbandListEntryTagInputModelList;
import org.phoenixctms.ctsms.web.util.DefaultSettings;
import org.phoenixctms.ctsms.web.util.JSValues;
import org.phoenixctms.ctsms.web.util.MessageCodes;
import org.phoenixctms.ctsms.web.util.Messages;
import org.phoenixctms.ctsms.web.util.SettingCodes;
import org.phoenixctms.ctsms.web.util.Settings;
import org.phoenixctms.ctsms.web.util.Settings.Bundle;
import org.phoenixctms.ctsms.web.util.WebUtil;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class ProbandListEntryTagValueBean extends ManagedBeanBase {

	private static int MAX_INPUT_FIELDS_PER_ROW = 10;

	public static void copyProbandListEntryTagValueOutToIn(ProbandListEntryTagValueInVO in, ProbandListEntryTagValueOutVO out) {
		if (in != null && out != null) {
			ProbandListEntryOutVO listEntryVO = out.getListEntry();
			ProbandListEntryTagOutVO tagVO = out.getTag();
			Collection<InputFieldSelectionSetValueOutVO> tagValueVOs = out.getSelectionValues();
			in.setBooleanValue(out.getBooleanValue());
			in.setDateValue(out.getDateValue());
			in.setTimeValue(out.getTimeValue());
			in.setFloatValue(out.getFloatValue());
			in.setId(out.getId());
			in.setListEntryId(listEntryVO == null ? null : listEntryVO.getId());
			in.setLongValue(out.getLongValue());
			ArrayList<Long> selectionValueIds = new ArrayList<Long>(tagValueVOs.size());
			Iterator<InputFieldSelectionSetValueOutVO> tagValueVOsIt = tagValueVOs.iterator();
			while (tagValueVOsIt.hasNext()) {
				selectionValueIds.add(tagValueVOsIt.next().getId());
			}
			in.setSelectionValueIds(selectionValueIds);
			in.setTagId(tagVO == null ? null : tagVO.getId());
			in.setTextValue(out.getTextValue());
			in.setTimestampValue(out.getTimestampValue());
			in.setInkValues(out.getInkValues());
			in.setVersion(out.getVersion());
		}
	}

	public static void copyProbandListEntryTagValuesInToJson(ArrayList<ProbandListEntryTagValueJsonVO> tagValuesOut, ArrayList<ProbandListEntryTagValueInVO> tagValuesIn,
			HashMap<Long, ProbandListEntryTagOutVO> tagVOsMap) {
		if (tagValuesOut != null) {
			tagValuesOut.clear();
			if (tagValuesIn != null && tagVOsMap != null) {
				tagValuesOut.ensureCapacity(tagValuesIn.size());
				Iterator<ProbandListEntryTagValueInVO> it = tagValuesIn.iterator();
				while (it.hasNext()) {
					ProbandListEntryTagValueInVO in = it.next();
					if (tagVOsMap.containsKey(in.getTagId())) {
						ProbandListEntryTagValueJsonVO out = new ProbandListEntryTagValueJsonVO();
						CommonUtil.copyProbandListEntryTagValueInToJson(out, in, tagVOsMap.get(in.getTagId()));
						tagValuesOut.add(out);
					}
				}
			}
		}
	}

	public static void copyProbandListEntryTagValuesOutToIn(ArrayList<ProbandListEntryTagValueInVO> tagValuesIn, Collection<ProbandListEntryTagValueOutVO> tagValuesOut,
			boolean keepTagValuesIn) {
		if (tagValuesIn != null) {
			HashMap<Long, ProbandListEntryTagValueInVO> tagValuesInMap = new HashMap<Long, ProbandListEntryTagValueInVO>(tagValuesIn.size());
			if (keepTagValuesIn) {
				Iterator<ProbandListEntryTagValueInVO> it = tagValuesIn.iterator();
				while (it.hasNext()) {
					ProbandListEntryTagValueInVO in = it.next();
					tagValuesInMap.put(in.getTagId(), in);
				}
			}
			tagValuesIn.clear();
			if (tagValuesOut != null) {
				tagValuesIn.ensureCapacity(tagValuesOut.size());
				Iterator<ProbandListEntryTagValueOutVO> it = tagValuesOut.iterator();
				while (it.hasNext()) {
					ProbandListEntryTagValueOutVO out = it.next();
					ProbandListEntryTagValueInVO in;
					if (tagValuesInMap.containsKey(out.getTag().getId())) {
						in = tagValuesInMap.get(out.getTag().getId());
					} else {
						in = new ProbandListEntryTagValueInVO();
						copyProbandListEntryTagValueOutToIn(in, out);
					}
					tagValuesIn.add(in);
				}
			}
		}
	}

	private Long probandListEntryId;
	private ProbandListEntryOutVO probandListEntry;
	private Paginator paginator;
	private ArrayList<ProbandListEntryTagValueInVO> tagValuesIn;
	private Collection<ProbandListEntryTagValueOutVO> tagValuesOut;
	private Collection<ProbandListEntryTagValueJsonVO> jsTagValuesOut;
	private ProbandListEntryTagInputModelList inputModels;
	private List<Object[]> paddedInputModels;
	private Collection<ProbandGroupOutVO> probandGroups;
	private Collection<VisitScheduleItemOutVO> visitScheduleItems;

	public ProbandListEntryTagValueBean() {
		super();
		paginator = new Paginator() {

			@Override
			protected Long getCount(Long... ids) {
				ProbandListEntryOutVO probandListEntry = WebUtil.getProbandListEntry(ids[0]);
				if (probandListEntry != null) {
					return WebUtil.getProbandListEntryTagCount(probandListEntry.getTrial().getId(), null);
				}
				return null;
			}

			@Override
			protected int getDefaultFieldsPerRow() {
				return Settings.getInt(SettingCodes.PROBAND_LIST_ENTRY_TAG_VALUES_DEFAULT_FIELDS_PER_ROW, Bundle.SETTINGS,
						DefaultSettings.PROBAND_LIST_ENTRY_TAG_VALUES_DEFAULT_FIELDS_PER_ROW);
			}

			@Override
			protected int getDefaultPageSize() {
				return Settings.getInt(SettingCodes.PROBAND_LIST_ENTRY_TAG_VALUES_DEFAULT_PAGE_SIZE, Bundle.SETTINGS,
						DefaultSettings.PROBAND_LIST_ENTRY_TAG_VALUES_DEFAULT_PAGE_SIZE);
			}

			@Override
			protected String getFirstPageButtonLabel() {
				return MessageCodes.PROBAND_LIST_ENTRY_TAG_VALUES_FIRST_PAGE_BUTTON_LABEL;
			}

			@Override
			protected String getLoadLabel() {
				return MessageCodes.PROBAND_LIST_ENTRY_TAG_VALUES_LOAD_LABEL;
			}

			@Override
			protected int getMaxFieldsPerRow() {
				return MAX_INPUT_FIELDS_PER_ROW;
			}

			@Override
			protected String getPageButtonLabel() {
				return MessageCodes.PROBAND_LIST_ENTRY_TAG_VALUES_PAGE_BUTTON_LABEL;
			}

			@Override
			protected ArrayList<String> getPageSizeStrings() {
				return Settings.getStringList(SettingCodes.PROBAND_LIST_ENTRY_TAG_VALUES_PAGE_SIZES, Bundle.SETTINGS,
						DefaultSettings.PROBAND_LIST_ENTRY_TAG_VALUES_PAGE_SIZES);
			}
		};
		this.change();
	}

	@Override
	public void appendRequestContextCallbackArgs(boolean operationSuccess) {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (requestContext != null) {
			requestContext.addCallbackParam(JSValues.AJAX_OPERATION_SUCCESS.toString(), operationSuccess);
			if (tagValuesIn != null && tagValuesOut != null && jsTagValuesOut != null && jsTagValuesOut.size() > 0) {
				ProbandListEntryOutVO listEntry = null;
				Collection<VisitScheduleItemOutVO> visitSchedule = null;
				Collection<ProbandGroupOutVO> groups = null;
				ArrayList<ProbandListEntryTagValueJsonVO> out = new ArrayList<ProbandListEntryTagValueJsonVO>(jsTagValuesOut.size());
				HashMap<Long, ProbandListEntryTagOutVO> tagVOsMap = new HashMap<Long, ProbandListEntryTagOutVO>(tagValuesOut.size());
				Iterator<ProbandListEntryTagValueOutVO> tagIt = tagValuesOut.iterator();
				while (tagIt.hasNext()) {
					ProbandListEntryTagValueOutVO tagValueVO = tagIt.next();
					listEntry = tagValueVO.getListEntry();
					ProbandListEntryTagOutVO tagVO = tagValueVO.getTag();
					if (!CommonUtil.isEmptyString(tagVO.getJsVariableName())) {
						tagVOsMap.put(tagVO.getId(), tagVO);
					}
				}
				copyProbandListEntryTagValuesInToJson(out, tagValuesIn, tagVOsMap);
				Iterator<ProbandListEntryTagValueJsonVO> jsTagValueIt = jsTagValuesOut.iterator();
				while (jsTagValueIt.hasNext()) {
					ProbandListEntryTagValueJsonVO jsTagValue = jsTagValueIt.next();
					if (!tagVOsMap.containsKey(jsTagValue.getTagId())) {
						out.add(jsTagValue);
					}
				}
				if (listEntry != null) {
					if (probandListEntry != null) {
						if (listEntry.getProband().getId() != probandListEntry.getProband().getId()) {
							visitSchedule = loadVisitScheduleItems(listEntry);
							groups = loadProbandGroups(probandListEntry);
						} else {
							//if (visitScheduleItemStartTagsCount != null && visitScheduleItemStartTagsCount > 0) {
							//	visitSchedule = loadVisitScheduleItems(listEntry);
							//} else {
							visitSchedule = visitScheduleItems;
							//}
							groups = probandGroups;
						}
					} else {
						visitSchedule = loadVisitScheduleItems(null);
						groups = loadProbandGroups(probandListEntry);
					}
				}
				requestContext.addCallbackParam(JSValues.AJAX_INPUT_FIELD_VARIABLE_VALUES_BASE64.toString(),
						JsUtil.encodeBase64(JsUtil.inputFieldVariableValueToJson(out), false));
				requestContext.addCallbackParam(JSValues.AJAX_INPUT_FIELD_PROBAND_LIST_ENTRY_BASE64.toString(),
						JsUtil.encodeBase64(JsUtil.voToJson(listEntry), false));
				requestContext.addCallbackParam(JSValues.AJAX_INPUT_FIELD_VISIT_SCHEDULE_ITEMS_BASE64.toString(), JsUtil.encodeBase64(JsUtil.voToJson(visitSchedule), false));
				requestContext.addCallbackParam(JSValues.AJAX_INPUT_FIELD_PROBAND_GROUPS_BASE64.toString(), JsUtil.encodeBase64(JsUtil.voToJson(groups), false));
				requestContext.addCallbackParam(JSValues.AJAX_INPUT_FIELD_ACTIVE_USER_BASE64.toString(),
						JsUtil.encodeBase64(JsUtil.voToJson(WebUtil.getUser()), false));
			}
		}
	}

	@Override
	protected String changeAction(Long id) {
		tagValuesOut = null;
		jsTagValuesOut = null;
		this.probandListEntryId = id;
		paginator.initPages(false, probandListEntryId);
		initIn(true, false);
		initSets();
		return CHANGE_OUTCOME;
	}

	private void clearInputModelErrorMsgs() {
		Iterator<ProbandListEntryTagInputModel> it = inputModels.iterator();
		while (it.hasNext()) {
			it.next().setErrorMessage(null);
		}
	}

	private void clearInputModelModifiedAnnotations() {
		Iterator<ProbandListEntryTagInputModel> it = inputModels.iterator();
		while (it.hasNext()) {
			it.next().setModifiedAnnotation(null);
		}
	}

	public List<Object[]> getInputModels() {
		return paddedInputModels;
	}

	@Override
	public String getModifiedAnnotation() {
		if (tagValuesOut != null && tagValuesOut.size() > 0) {
			Long version = null;
			UserOutVO modifiedUser = null;
			Date modifiedTimestamp = null;
			Iterator<ProbandListEntryTagValueOutVO> it = tagValuesOut.iterator();
			while (it.hasNext()) {
				ProbandListEntryTagValueOutVO tagValueVO = it.next();
				Date tagValueModifiedTimestamp = tagValueVO.getModifiedTimestamp();
				if (modifiedTimestamp == null || (tagValueModifiedTimestamp != null && modifiedTimestamp.compareTo(tagValueModifiedTimestamp) < 0)) {
					modifiedUser = tagValueVO.getModifiedUser();
					modifiedTimestamp = tagValueModifiedTimestamp;
				}
				if (version == null) {
					version = tagValueVO.getVersion();
				} else {
					version = Math.max(version.longValue(), tagValueVO.getVersion());
				}
			}
			if (version == null || modifiedUser == null || modifiedTimestamp == null) {
				return super.getModifiedAnnotation();
			} else {
				return WebUtil.getModifiedAnnotation(version.longValue(), modifiedUser, modifiedTimestamp);
			}
		} else {
			return super.getModifiedAnnotation();
		}
	}

	public Paginator getPaginator() {
		return paginator;
	}

	public StreamedContent getProbandListEntryTagPdfStreamedContent(boolean blank) throws Exception {
		if (probandListEntry != null) {
			try {
				ProbandListEntryTagsPDFVO probandListEntryTagPdf = WebUtil.getServiceLocator().getTrialService().renderProbandListEntryTags(WebUtil.getAuthentication(),
						probandListEntry.getTrial().getId(), probandListEntry.getProband().getId(), blank);
				return new DefaultStreamedContent(new ByteArrayInputStream(probandListEntryTagPdf.getDocumentDatas()), probandListEntryTagPdf.getContentType().getMimeType(),
						probandListEntryTagPdf.getFileName());
			} catch (AuthenticationException e) {
				WebUtil.publishException(e);
				throw e;
			} catch (AuthorisationException | ServiceException | IllegalArgumentException e) {
				throw e;
			}
		}
		return null;
	}

	public void handleFieldsPerRowChange() {
		paddedInputModels = inputModels.createPaddedList(paginator.getFieldsPerRow(), MAX_INPUT_FIELDS_PER_ROW,
				Settings.getInt(SettingCodes.PROBAND_LIST_TAG_INPUTS_GRID_COLUMNS_THRESHOLD_WIDTH, Bundle.SETTINGS,
						DefaultSettings.PROBAND_LIST_TAG_INPUTS_GRID_COLUMNS_THRESHOLD_WIDTH));
		updateInputModelModifiedAnnotations();
		appendRequestContextCallbackArgs(true);
	}

	public void handleFirstPage() {
		paginator.setToFirstPage();
		handlePageChange();
	}

	public void handleLastPage() {
		paginator.setToLastPage();
		handlePageChange();
	}

	public void handleNextPage() {
		paginator.setToNextPage();
		handlePageChange();
	}

	public void handlePageChange() {
		tagValuesOut = null;
		jsTagValuesOut = null;
		paginator.updatePSF();
		initIn(false, true);
		initSets();
		appendRequestContextCallbackArgs(true);
	}

	public void handlePageSizeChange() {
		tagValuesOut = null;
		jsTagValuesOut = null;
		paginator.initPages(false, probandListEntryId);
		initIn(false, true);
		initSets();
		appendRequestContextCallbackArgs(true);
	}

	public void handlePrevPage() {
		paginator.setToPrevPage();
		handlePageChange();
	}

	protected void initIn(boolean loadAllJsValues, boolean keepTagValuesIn) {
		if (tagValuesIn == null) {
			tagValuesIn = new ArrayList<ProbandListEntryTagValueInVO>();
		}
		if (probandListEntryId == null) {
			tagValuesIn.clear();
			if (tagValuesOut != null) {
				tagValuesOut.clear();
			}
			if (jsTagValuesOut != null) {
				jsTagValuesOut.clear();
			}
		} else {
			if (tagValuesOut == null || jsTagValuesOut == null) {
				try {
					portionTagValues(WebUtil.getServiceLocator().getTrialService()
							.getProbandListEntryTagValues(WebUtil.getAuthentication(), probandListEntryId, true, loadAllJsValues, paginator.getPsf()));
				} catch (ServiceException | AuthorisationException | IllegalArgumentException e) {
				} catch (AuthenticationException e) {
					WebUtil.publishException(e);
				}
			}
			copyProbandListEntryTagValuesOutToIn(tagValuesIn, tagValuesOut, keepTagValuesIn);
		}
	}

	private void initSets() {
		inputModels = new ProbandListEntryTagInputModelList(tagValuesIn);
		paddedInputModels = inputModels.createPaddedList(paginator.getFieldsPerRow(), MAX_INPUT_FIELDS_PER_ROW,
				Settings.getInt(SettingCodes.PROBAND_LIST_TAG_INPUTS_GRID_COLUMNS_THRESHOLD_WIDTH, Bundle.SETTINGS,
						DefaultSettings.PROBAND_LIST_TAG_INPUTS_GRID_COLUMNS_THRESHOLD_WIDTH));
		probandListEntry = WebUtil.getProbandListEntry(probandListEntryId);
		visitScheduleItems = loadVisitScheduleItems(probandListEntry);
		probandGroups = loadProbandGroups(probandListEntry);
		updateInputModelModifiedAnnotations();
	}

	@Override
	public boolean isCreateable() {
		return false;
	}

	@Override
	public boolean isCreated() {
		if (tagValuesOut != null && tagValuesOut.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isEditable() {
		return ((probandListEntryId != null && tagValuesIn.size() > 0 && !WebUtil.isTrialLocked(probandListEntry) && !WebUtil.isProbandLocked(probandListEntry)) ? true : false);
	}

	@Override
	public boolean isRemovable() {
		return false;
	}

	@Override
	public String loadAction() {
		tagValuesOut = null;
		jsTagValuesOut = null;
		try {
			portionTagValues(WebUtil.getServiceLocator().getTrialService()
					.getProbandListEntryTagValues(WebUtil.getAuthentication(), probandListEntryId, true, true, paginator.getPsf()));
			return LOAD_OUTCOME;
		} catch (ServiceException e) {
			Messages.addMessageClientId("probandListEntryTagValueMessages", FacesMessage.SEVERITY_ERROR, e.getMessage());
		} catch (AuthenticationException e) {
			Messages.addMessageClientId("probandListEntryTagValueMessages", FacesMessage.SEVERITY_ERROR, e.getMessage());
			WebUtil.publishException(e);
		} catch (AuthorisationException e) {
			Messages.addMessageClientId("probandListEntryTagValueMessages", FacesMessage.SEVERITY_ERROR, e.getMessage());
		} catch (IllegalArgumentException e) {
			Messages.addMessageClientId("probandListEntryTagValueMessages", FacesMessage.SEVERITY_ERROR, e.getMessage());
		} finally {
			initIn(true, false);
			initSets();
		}
		return ERROR_OUTCOME;
	}

	private Collection<ProbandGroupOutVO> loadProbandGroups(ProbandListEntryOutVO listEntry) {
		if (listEntry != null) {
			try {
				return WebUtil
						.getServiceLocator()
						.getTrialService().getProbandGroupList(WebUtil.getAuthentication(), listEntry.getTrial().getId(), null);
			} catch (ServiceException | AuthorisationException | IllegalArgumentException e) {
			} catch (AuthenticationException e) {
				WebUtil.publishException(e);
			}
		}
		return null;
	}

	private void portionTagValues(ProbandListEntryTagValuesOutVO tagValues) {
		if (tagValues != null) {
			tagValuesOut = tagValues.getPageValues();
			jsTagValuesOut = tagValues.getJsValues();
		} else {
			tagValuesOut = null;
			jsTagValuesOut = null;
		}
	}

	@Override
	public String resetAction() {
		tagValuesOut = null;
		jsTagValuesOut = null;
		initIn(true, false);
		initSets();
		Iterator<ProbandListEntryTagInputModel> it = inputModels.iterator();
		while (it.hasNext()) {
			it.next().applyPresets();
		}
		return RESET_OUTCOME;
	}

	private void setInputModelErrorMsgs(Object data) {
		HashMap<Long, String> errorMessagesMap;
		try {
			errorMessagesMap = (HashMap<Long, String>) data;
		} catch (ClassCastException e) {
			errorMessagesMap = null;
		}
		if (errorMessagesMap != null && errorMessagesMap.size() > 0) {
			Iterator<ProbandListEntryTagInputModel> it = inputModels.iterator();
			while (it.hasNext()) {
				ProbandListEntryTagInputModel inputModel = it.next();
				if (errorMessagesMap.containsKey(inputModel.getProbandListTagId())) {
					inputModel.setErrorMessage(errorMessagesMap.get(inputModel.getProbandListTagId()));
				} else {
					inputModel.setErrorMessage(null);
				}
			}
		} else {
			clearInputModelErrorMsgs();
		}
	}

	@Override
	public String updateAction() {
		try {
			portionTagValues(WebUtil.getServiceLocator().getTrialService()
					.setProbandListEntryTagValues(WebUtil.getAuthentication(), new HashSet<ProbandListEntryTagValueInVO>(tagValuesIn), false));
			initIn(false, false);
			initSets();
			addOperationSuccessMessage("probandListEntryTagValueMessages", MessageCodes.UPDATE_OPERATION_SUCCESSFUL);
			return UPDATE_OUTCOME;
		} catch (ServiceException e) {
			setInputModelErrorMsgs(e.getData());
			Messages.addMessageClientId("probandListEntryTagValueMessages", FacesMessage.SEVERITY_ERROR, e.getMessage());
		} catch (AuthenticationException e) {
			Messages.addMessageClientId("probandListEntryTagValueMessages", FacesMessage.SEVERITY_ERROR, e.getMessage());
			WebUtil.publishException(e);
		} catch (AuthorisationException | IllegalArgumentException e) {
			Messages.addMessageClientId("probandListEntryTagValueMessages", FacesMessage.SEVERITY_ERROR, e.getMessage());
		}
		return ERROR_OUTCOME;
	}

	private void updateInputModelModifiedAnnotations() {
		if (tagValuesOut != null && tagValuesOut.size() > 0) {
			HashMap<Long, ProbandListEntryTagValueOutVO> tagValuesOutMap = new HashMap<Long, ProbandListEntryTagValueOutVO>(tagValuesOut.size());
			Iterator<ProbandListEntryTagValueOutVO> inquiryValuesOutIt = tagValuesOut.iterator();
			while (inquiryValuesOutIt.hasNext()) {
				ProbandListEntryTagValueOutVO out = inquiryValuesOutIt.next();
				tagValuesOutMap.put(out.getTag().getId(), out);
			}
			Iterator<ProbandListEntryTagInputModel> it = inputModels.iterator();
			while (it.hasNext()) {
				ProbandListEntryTagInputModel inputModel = it.next();
				if (tagValuesOutMap.containsKey(inputModel.getProbandListTagId())) {
					inputModel.setModifiedAnnotation(tagValuesOutMap.get(inputModel.getProbandListTagId()));
				} else {
					inputModel.setModifiedAnnotation(null);
				}
			}
		} else {
			clearInputModelModifiedAnnotations();
		}
	}

	private Collection<VisitScheduleItemOutVO> loadVisitScheduleItems(ProbandListEntryOutVO listEntry) {
		if (listEntry != null) {
			try {
				return WebUtil
						.getServiceLocator()
						.getTrialService()
						.getVisitScheduleItemList(WebUtil.getAuthentication(), listEntry.getTrial().getId(), listEntry.getGroup() != null ? listEntry.getGroup().getId() : null,
								null, listEntry.getProband().getId(),
								true, null);
			} catch (ServiceException | AuthorisationException | IllegalArgumentException e) {
			} catch (AuthenticationException e) {
				WebUtil.publishException(e);
			}
		}
		return null;
	}
}
