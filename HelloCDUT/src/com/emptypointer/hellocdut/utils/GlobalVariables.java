package com.emptypointer.hellocdut.utils;

/**
 * 用于存储程序全局数据的工具类
 *
 * @author Sequarius
 */
public class GlobalVariables {
    //serverhost
//	public static final String	SERVICE_HOST_USER_SYSTEM	= "http://www.hellocdut.com/api/pointer/empty/userSystemApiTest/apiRequestPro.php";
//	public static final String SERVICE_HOST_BIND_SYSTEM="http://www.hellocdut.com/api/pointer/empty/bindingApi/verifyPro.php";
//	public static final String SERVICE_HOST_UNBIND_SYSTEM="http://www.hellocdut.com/api/pointer/empty/unbindingApi/unbindingPro.php";
//	public static final String SERVICE_HOST_FILE_SYSTEM="http://www.hellocdut.com/api/pointer/empty/qiniuApi/qiniuPro.php";
//	public static final String SERVICE_HOST_QUERYSYSTEM="http://www.hellocdut.com/api/pointer/empty/queryApiTest/queryPro.php";
//	public static final String SERVICE_HOST_UPDATE="http://www.hellocdut.com/api/pointer/empty/queryUpdate/queryPro.php";
//	public static final String SERVICE_HOST_CAMPUS_CARD="http://www.hellocdut.com/api/pointer/empty/queryCampusInfo/queryPro.php";
    public static final String SERVICE_HOST= "http://www.hellocdut.com/api/release/index.php";

    public static final String SERVICE_HOST_USER_SYSTEM = SERVICE_HOST;
    public static final String SERVICE_HOST_BIND_SYSTEM = SERVICE_HOST;
    public static final String SERVICE_HOST_UNBIND_SYSTEM = SERVICE_HOST;
    public static final String SERVICE_HOST_FILE_SYSTEM = SERVICE_HOST;
    public static final String SERVICE_HOST_QUERYSYSTEM = SERVICE_HOST;
    public static final String SERVICE_HOST_UPDATE = SERVICE_HOST;
    public static final String SERVICE_HOST_CAMPUS_CARD = SERVICE_HOST;
    public static final String SERVICE_HOST_QUERY_BOOK = SERVICE_HOST;
    public static final String SERVICE_HOST_ADDONES = SERVICE_HOST;


    public static final String SERVICE_HOST_AGREEMENT = "http://www.hellocdut.com/agreement.html";

    public static final String ABOUT_URL = "http://www.emptypointer.com/about/";

    public static final String ERRO_MESSAGE_RELOG = "登陆已过期，请重新登录。";


    public static final String SHARED_PERFERENCR_INIT = "data_init";

    public static final String SHARED_PERFERENCR_SETTING = "setting";
    //Actions
    //登陆

    public static final String ACTION_MAIN_ACTIVITY = "com.emptypointer.action.MainActivity";
    //注册
    public static final String ACTION_REGISTER = "com.emptypointer.action.Register";
    //找回账号密码
    public static final String ACTION_RETRIEVE = "com.emptypointer.action.Retrieve";
    //日程
    public static final String ACTION_SCHEDULE = "com.emptypointer.action.Schedule";
    //教務新聞列表
    public static final String ACTION_AAONEWS = "com.emptypointer.action.AAONews";
    //教務新聞詳情
    public static final String ACTION_AAONEWS_DETAIL = "com.emptypointer.action.AAONewsDetail";
    //綁定賬號管理
    public static final String ACTION_ACCOUNTMANAGE = "com.emptypointer.action.AccountManage";
    //設置
    public static final String ACTION_SETTING = "com.emptypointer.action.Setting";
    //用戶信息
    public static final String ACTION_USERINFO = "com.emptypointer.action.UserInfo";
    //考試管理
    public static final String ACTION_EXAMMANAGE = "com.emptypointer.action.ExamManage";
    //收藏管理
    public static final String ACTION_COLLECTIONMANAGE = "com.emptypointer.action.CollectionManage";
    //查询
    public static final String ACTION_QUERY = "com.emptypointer.action.Query";
    //查询教室
    public static final String ACTION_QUERY_CLASS_ROOM = "com.emptypointer.action.QueryClassroom";
    //查询成绩
    public static final String ACTION_QUERY_GRADE = "com.emptypointer.action.QueryGrade";
    //查询等级考试
    public static final String ACTION_QUERY_NATIONAL_EXAM = "com.emptypointer.action.QueryNationalExam";
    //查询短号
    public static final String ACTION_QUERY_TEL = "com.emptypointer.action.QueryTel";
    //查询教学计划
    public static final String ACTION_QUERY_TEACH_PLAN = "com.emptypointer.action.QueryTeach";
    //添加插件
    public static final String ACTION_ADD_ONES = "com.emptypointer.action.AddOnes";
    //添加联系人
    public static final String ACTION_ADD_CONTACT = "com.emptypointer.action.AddContact";
    //验证处理
    public static final String ACTION_INVITATION_MANAGE = "com.emptypointer.action.InvitationManage";
    //搜索
    public static final String ACTION_SEARCH = "com.emptypointer.action.Search";
    //绑定
    public static final String ACTION_BIND = "com.emptypointer.action.Bind";
    //修改资料
    public static final String ACTION_MODIFY_INFO = "com.emptypointer.action.ModifyInfo";
    //聊天
    public static final String ACTION_CHAT = "com.emptypointer.action.Chat";
    //设置详情
    public static final String ACTION_SETTING_DETAIL = "com.emptypointer.action.SettingDetail";
    //上傳頭像
    public static final String ACTION_UPLOAD_AVATAR = "com.emptypointer.action.UploadAvatar";
    //修改密碼
    public static final String ACTION_MODIFY_PASSWORD = "com.emptypointer.action.ModifyPassword";
    //图书馆
    public static final String ACTION_LIBRARY = "com.emptypointer.action.Library";
    //黑名单
    public static final String ACTION_IGNORE = "com.emptypointer.action.IgnoreList";
    //一卡通
    public static final String ACTION_CAMPUS_CARD = "com.emptypointer.action.CampusCard";


    //intent extras
    public static final String INTENT_EXTRA_AAONEWS_DETAIL = "com.emptypointer.extra.aaonewsdetail";

    public static final String INTENT_EXTRA_BIND_MODE = "com.emptypointer.extra.bind_mode";

    public static final String INTENT_EXTRA_MODIFY = "com.emptypointer.extra.modify";
    public static final String INTENT_EXTRA_SETTINGDETAIL = "com.emptypointer.extra.SettingDetail";

    public static final String INTENT_EXTRA_AAONEWS_TITLE = "com.emptypointer.extra.aaonewtittle";

    /**
     * 数据库表名
     */
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String AAO_USERNAME = "item_aao";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";


}
