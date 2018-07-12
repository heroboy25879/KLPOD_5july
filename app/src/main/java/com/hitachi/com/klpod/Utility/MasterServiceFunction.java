package com.hitachi.com.klpod.Utility;

public class MasterServiceFunction {
    // on Cloud
    //private String urlService = "http://203.154.103.42/KLwebservice/";
    private String urlService = "http://172.23.191.13/KLwebservice/";
    private String methodService = "MethodService.svc";

    public String getUrlService() {
        return urlService;
    }

    //about url web service
    // /GetUserLogin/{pUser}/{pPassword}/{pIMEI}
    private String GetUserLogin = urlService + methodService + "/GetUserLogin";
    // /GetOutboundDate/{pVehiclesCode}
    private String GetOutboundDate = urlService + methodService + "/GetOutboundDate";
    ///GetDeliveryPlan/{pVehiclesCode}/{pOutboundDate
    private String GetDeliveryPlan = urlService + methodService + "/GetDeliveryPlan";
    ///UpdateStartJob/{pDeliveryNo}/{pLatitudeDeparture}/{pLongitudeDeparture}/{pIMEI}/{pUserUpdate}
    private String UpdateStartJob = urlService + methodService + "/UpdateStartJob";
    ///CheckStartJob/{pDeliveryNo}
    private String CheckStartJob = urlService + methodService + "/CheckStartJob";
    ///CheckEndJob/{pDeliveryNo}
    private String CheckEndJob = urlService + methodService + "/CheckEndJob";
    ///GetDeliveryPlanDetail/{pDeliveryDetailNo}/{pOwnerCode}
    private String GetDeliveryPlanDetail = urlService + methodService + "/GetDeliveryPlanDetail";
    ///UpdateArrivalTime/{pDeliveryDetailNo}/{pLatitude}/{pLongitude}/{pUserUpdate}
    private String UpdateArrivalTime = urlService + methodService + "/UpdateArrivalTime";
    ///GetContainer/{pDeliveryDetailNo}/{pStoreCode}
    private String GetContainer = urlService + methodService + "/GetContainer";
    ///InsertTrDeliveryReturn/{pDeliveryDetailNo}/{pStoreCode}/{pContainerCode}/{pContainerQty}/{pUserCreate}
    private String InsertTrDeliveryReturn = urlService + methodService + "/InsertTrDeliveryReturn";
    ///UpdateDepartureTime/{pDeliveryDetailNo}/{pUserUpdate}
    private String UpdateDepartureTime = urlService + methodService + "/UpdateDepartureTime";
    ///UpdateReceiverName/{pDeliveryDetailNo}/{pReceiverName}/{pUserUpdate}
    private String UpdateReceiverName = urlService + methodService + "/UpdateReceiverName";
    ///UpdateStartJob/{pDeliveryNo}/{pUserUpdate}
    private String UpdateEndJob = urlService + methodService + "/UpdateEndJob";
    ///UpdateStartJob/{pIMEI}
    private String UpdateUserlogout = urlService + methodService + "/UpdateUserlogout";
    ///InsertImage/{pDeliveryDetailNo}/{pImageSequence}/{pImageName}/{pUserCreate}
    private String InsertImage = urlService + methodService + "/InsertImage";

    public String getInsertImage() {
        return InsertImage;
    }

    public String getUpdateUserlogout() {
        return UpdateUserlogout;
    }

    public String getUpdateEndJob() {
        return UpdateEndJob;
    }

    public String getUpdateReceiverName() {
        return UpdateReceiverName;
    }

    public String getUpdateDepartureTime() {
        return UpdateDepartureTime;
    }

    public String getInsertTrDeliveryReturn() {
        return InsertTrDeliveryReturn;
    }

    public String getGetContainer() {
        return GetContainer;
    }

    public String getUpdateArrivalTime() {
        return UpdateArrivalTime;
    }

    public String getGetDeliveryPlanDetail() {
        return GetDeliveryPlanDetail;
    }

    public String getGetDeliveryPlan() {
        return GetDeliveryPlan;
    }

    public String getGetUserLogin() {
        return GetUserLogin;
    }

    public String getGetOutboundDate() {
        return GetOutboundDate;
    }

    public String getUpdateStartJob() {
        return UpdateStartJob;
    }

    public String getCheckStartJob() {
        return CheckStartJob;
    }

    public String getCheckEndJob() {
        return CheckEndJob;
    }
}
