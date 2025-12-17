package com.insightfinder.base;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalConstant {

  // General constant
  public static final long ZERO_LONG = 0l;
  public static final double ZERO_DOUBLE = 0.0;
  public static final float ZERO_FLOAT = 0.0f;
  public static final int ZERO_INT = 0;
  public static final String IF_ENCRYPTION_SAUCE = "INsIGhT_FOr_FAcT";
  public static final int MODEL_NEURON_CHUNK_LIMIT = 3000000;
  public static final String DURATION = "duration";
  public static final int DEFAULT_INSTANCE_DOWN_DETECTION_NUMBER = 50;
  public static final double DEFAULT_LOSS_RATIO = 0.1;
  public static final String PERCENTAGE = "percentage";
  public static final String REPLAY_FLAG = "replayFlag";
  public static final int TIMESTAMP_LENGTH = 13;
  public static final String NEW_LINE = "\n";
  public static final String COMMA = ",";
  public static final String SEMICOLON = ";";
  public static final String DOT = ".";
  public static final String ELLIPSIS = "...";
  public static final String TIMESTAMP_STRING = "timestamp";
  public static final String TYPE_STRING = "type";
  public static final String ANOMALY_TYPE_STRING = "anomalyType";
  public static final String AT = "@";
  public static final String EMPTY_STRING = "";
  public static final String LESS_THAN_OP = "<";
  public static final String LARGER_THAN_OP = ">";
  public static final String MULTIPLE_LINE_SEP = "\\n{2,}|(\\n\\r){2,}";
  public static final String LINE_SEPARATOR = "\n";
  public static final String APPLICATION_JSON = "application/json";
  public static final String UNDERSCORE = "_";
  public static final String COLON = ":";
  public static final String LEFT_PARENTHESIS = "[";
  public static final String RIGHT_PARENTHESIS = "]";
  public static final String LEFT_BRACKET = "(";
  public static final String RIGHT_BRACKET = ")";
  //k8s instance splitter namespaceName>podName>containerNanme
  public static final String RIGHT_ARROW = ">>>";
  public static final char RIGHT_CURLY_PARENTHESIS = '}';
  public static final String OR_SYMBOL = "|";
  public static final String DELETE = "Delete";
  public static final String OPERATION = "operation";
  public static final String DATA = "data";
  public static final int TRUE = 1;
  public static final int FALSE = 0;
  public static final String FALSE_STR = "false";
  public static final String CAUSAL_KEY = "causalKey";
  public static final String INCIDENT_KEY = "incidentKey";
  public static final String NAN = "NaN";
  public static final String WHITE_SPACE = "\\s";
  public static final String AND_SYMBOL = "&";
  public static final String QUESTION_MARK = "?";
  public static final String METRIC_STR = "metric";
  public static final double PERCENT_BASE = 100;
  public static final String INSTANCE_NAME = "instanceName";
  public static final String HTTP_PROTOCOL = "http://";
  public static final String RAWDATA_DIFF = "Diff";
  public static final String RAWDATA_CLUSTER = "Cluster";
  public static final String START_TIMESTAMP_STRING = "startTimestamp";
  public static final String END_TIMESTAMP_STRING = "endTimestamp";
  public static final String START_TIME_MILLIS = "startTimeMillis";
  public static final String END_TIME_MILLIS = "endTimeMillis";
  public static final String START_TIME = "startTime";
  public static final String END_TIME = "endTime";
  public static final String PARAMS = "params";
  public static final String METRIC_DATA_WITH_PREDICTION = "_MetricDataWithUBLPrediction";
  // this factor times the total historical health score
  public static final float MAX_99_PERCENTILE = 999f;
  public static final int PERCENTILE_VALUE = 99;
  public static final String THRESHOLD_VIOLATION = "Threshold Violation";
  public static final String METRIC_ALERT = "MetricAlert";
  public static final String UBL_ANOMALY = "ublAnomaly";
  public static final float THRESHOLD_VIOLATION_BASE_SCORE = 10.0f;
  public static final String METRIC_PREDICTION_DETECTION_QUEUE = "metric-prediction-detection-queue";
  public static String SYSTEM_CONFIG_QUERY_KEY = "insightFinder";
  public static String EMAIL_SETTING_FREQUENCY_ONETIME = "oneTime";
  public static String EMAIL_SETTING_FREQUENCY_ALWAYS = "always";
  public static final int COLUMN_LENGTH_THRESHOLD = 1000;
  public static final String specialDayRange = "range";
  public static final String specialDayDaily = "day";
  public static final String specialDayMonth = "month";
  public static final String specialDayWeek = "week";
  public static final String specialDayFirst = "first";
  public static final String specialDayEnd = "end";
  public static final String clauseEQ = "EQ";
  public static final String clauseGTE = "GTE";
  public static final String clauseIN = "IN";
  public static final String clauseLE = "LE";
  public static final String HIGHER = "Higher";
  public static final String DELAY = "delay";
  public static final String gvKPIViloation = "KPIViloation";
  public static final String gvFuture = "future";
  public static final String gvCurrent = "current";
  public static final float GV_INCIDENT_DEFAULT_RATIO = 1000.0f;
  public static final float GV_DEPLOYMENT_DEFAULT_RATIO = ZERO_FLOAT;
  public static final String PATTERN_IDS = "patternIds";
  public static final String PATTERN_ID = "patternId";
  public static final String PROJECT_NAME = "projectName";
  public static final String USER_NAME = "userName";
  public static final String LOGIN_DATE = "loginDate";
  public static final String gvPast = "past";
  public static final String GV_DEFAULT_ENV = "PROD";
  public static final String PATTERN_GREATER = "higher";
  public static final String PATTERN_LESS = "lower";
  public static final double absoluteSmallValue1 = 0.1;
  public static final double absoluteSmallValue2 = 5;
  public static final float NINETY_NINE_PERCENTILE_PARAM = 3.0f;
  public static final String DEFAULT_HEALTH_SCORE_CALCULATION = "STD";
  public static final float INIT_99_PERCENTILE = -1.0f;
  public static final double ONE_HUNDRED_HEALTH_SCORE = 100.0;
  public static final String METRIC_NAME_LOWER_CASE = "metricName";
  public static final String METRIC_DIRECTION = "metricDirection";
  public static final String DAILY_TIMESTAMP = "dailyTimestamp";
  public final static String CLUSTER_CUSTOM = "k8sCustom";
  public final static String CLUSTER_GKE = "k8sGke";
  public final static String ACTION_TYPE_K8S = "k8s";
  public final static String ACTION_TYPE_SCRIPT = "script";
  public final static String K8S_DEFAULT_ACTION_SCALE_UP_TO = "scaleUpTo";
  public final static String K8S_DEFAULT_ACTION_SCALE_UP_DELTA = "scaleUpBy";
  public final static String K8S_DEFAULT_ACTION_SCALE_DOWN_TO = "scaleDownTo";
  public final static String K8S_DEFAULT_ACTION_SCALE_DOWN_DELTA = "scaleDownBy";

  public final static String SCRIPT_DEFAULT_ACTION_SERVER_ID = "serverId";
  public final static String SCRIPT_DEFAULT_ACTION_CMD = "cmd";


  // Cassandra client side timeout settingz
  public static final int CASSANDRA_STORE_CONNECTION_TIMEOUT = 240000;
  public static final int CASSANDRA_STORE_READ_TIMEOUT = 240000;
  public static final String specialDayEndOfMonth = "Month end";

  // max number of keywords/episodes (for total number of keywords and episodes, this could be 30)
  public static final long ONE_MINUTE = 60000l;
  public static final int ONE_MINUTE_IN_SEC = 60;
  // Overlapping event tolerance amount (5 minutes in ms)
  public static final int DEFAULT_LOG_DETECTION_SIZE = 30000;
  public static final int DEFAULT_LOG_DETECTION_MIN_COUNT = 10000;
  public static final long ONE_MILLIS_IN_MILLIS = 1L;
  public static final long HALF_SECOND_IN_MILLIS = 500l;
  public static final long ONE_SECOND_IN_MILLIS = 1000l;
  public static final long ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
  public static final long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
  public static final long NANOSECONDS_TO_MILLIS = 1000000l;
  public static final long ONE_MILLION = 1000000l;
  public static final long ONE_YEAR_IN_MILLIS = ONE_DAY_IN_MILLIS * 365;
  public static final int TWO_YEAR_IN_SEC = 61516800;
  public static final int THREE_DAY_IN_SEC = 259200;
  // time interval of histograms
  public static final long FIFTEEN_MINS_IN_MILLIS = 1000 * 60 * 15;
  public static final long TEN_MINS_IN_MILLIS = 1000 * 60 * 10;
  public static final long FIVE_MINS_IN_MILLIS = 1000 * 60 * 5;
  public static final long ONE_WEEK_IN_MILLIS = ONE_DAY_IN_MILLIS * 7;
  public static final long ONE_MONTH_IN_MILLIS = ONE_DAY_IN_MILLIS * 31;
  public static final long SMALLEST_ONE_MONTH_IN_MILLIS = ONE_DAY_IN_MILLIS * 28;

  public static final String chunkingIndexFileDirectory = "chunks";
  public static final int minQueryKeywordLength = 2;

  public static final int FREQUENT_EPISODE_MIN_SUPPORT = 5; // the default
  // min_support when
  // doing
  // cronfrequencyepisodemining
  public static final int patternSequenceMaxSequenceLength = 10;
  public static final int ALIGN_SAMPLING_INTERVAL_THRESHOLD = 1;
  public static final long projectInfoUpdateWindow = 24 * 3600000l;
  public static final long dataPrefetchWindowLen = 10 * ONE_MINUTE;

  public static final long TWO_HUNDREDS_YEAR_LATER = 7999311317000L;
  public static final long CUSTOM_modelSpan_monthly = 30 * 24 * 3600000l;
  public static final long maxSearchDays = 15 * 24 * 60 * 60 * 1000l;
  public static final long maxSearchMonths = 7 * ONE_MONTH_IN_MILLIS;
  public static final String gcsEventDataBucketPrefix = "events";
  public static final String gcsLogDataBucketPrefix = "log_project_data";
  public static final String COMPONENT_PREFIX = "Component_";

  // project status
  public static final String PROJECT_WORKING_FINE_STATUS = "Detecting Anomalies";
  public static final String PROJECT_RECOVERED_STATUS = "Recovered";
  public static final String PROJECT_LOSING_DATA_STATUS = "Missing data";
  public static final String PROJECT_INITIALIZING_STATUS = "Initializing: no data";
  public static final String PROJECT_COLLECTING_STATUS = "Initializing: data received no model"; // no
  public static final Integer PROJECT_DO_DETECTION_STATUS = 1;
  public static final Integer PROJECT_DEFAULT_CALCULATED_DAYS = 7;
  // model
  // yet


  public static final String PROJECT_DELETING_STATUS = "Deleting";
  public static final String PROJECT_DELETING_DATA_STATUS = "DeletingData";
  public static final String PROJECT_PURGING_DATA_STATUS = "PurgingData";
  public static final String PROJECT_MODEL_CREATED_STATUS = "Initializing:model created";


  public static final long cronLogDetectionWindow = 20 * 60000l;
  public static final int CRON_METRIC_DETECTION_DATA_POINTS = 15;
  public static final float DETECTION_ZERO_VALUE = 0.001f;
  public static final int CRON_AWS_S3_METRIC_DETECTION_DATA_POINTS = 50;


  // bootstrap
  // assuming cron runs every 10 minutes
  public static final int bootstrapCheckMaxCountAgentProject = 6 * 6;
  // custom project agent name(insightAgentName)
  public static final String CUSTOM_PROJECT_CONTAINER_STREAMING = "containerStreaming";
  public static final String CUSTOM_PROJECT_CONTAINER_REPLAY = "containerReplay";
  public static final String customProjectcGroup = "cgroup";
  public static final String customProjectcAdvisor = "cadvisor";
  public static final String customProjectDemonset = "daemonset";
  public static Set<String> containerNameSet;

  static {
    containerNameSet = new HashSet<>();
    containerNameSet.add(customProjectcGroup);
    containerNameSet.add(customProjectcAdvisor);
    containerNameSet.add(customProjectDemonset);
    containerNameSet.add(CUSTOM_PROJECT_CONTAINER_STREAMING);
    containerNameSet.add(CUSTOM_PROJECT_CONTAINER_REPLAY);
  }

  // agent project file type
  public static final int FileProjectTypeAgentLogFile = 0;
  public static final int FileProjectTypeAgentMetricFile = 1;
  public static final int FileProjectTypeAgentNonFile = 2;
  public static final int FileProjectTypeNonAgent = 3;

  // changed to 22 from 24 hour to avoid model shifting later by 1 day
  public static final float NORMALIZATION_CONSTANT_PCT = 50;
  public static final float NORMALIZATION_CONSTANT_MAX = 100;
  public static final int DATA_USAGE_UNIT_SIZE = 1000000; // bytes

  // detection post processing
  // event summary and details
  public static final String INITIALIZING = "Initializing";
  public static final String PROCESSING = "Processing";
  public static final String FINISH = "Finish";
  public final static String event_CUSTOM_ACTION_SUCCESS = "Sent action: ";
  public final static String event_CUSTOM_ACTION_FAILED = "Action failed: ";
  public final static String event_DISK_CLEANUP_SUCCESS = "Clean Disk";
  public static final String eventEmailNotSentStatus = "Not sent";
  public static final double eventAnomalyScoreMin = 1.0;
  public static final double eventAnomalyScoreMax = 100.0;

  // --UBL Parameters
  public static final int hintNumber = 20;
  public static final String star = "*";
  public static int xDim = 32;
  public static int yDim = 32;
  public static final int DEFAULT_CLUSTER_NID_THRESHOLD = xDim * yDim;
  public static int cValue = 1;
  public static final int nTrainingIterations = 5;
  public static final int smoothing = -1;
  public static final String newInstance = "addedInstance";
  public static final long INSTANCE_LOST_DURATION_THRESHOLD = ONE_HOUR_IN_MILLIS;

  // instance meta data
  public static final String dynamoDBInstanceEnv = "DynamoDB";
  public static final long METRIC_DEFAULT_ANOMALY_DAMPENING = ONE_HOUR_IN_MILLIS * 14;
  // post detection checks
  public static final double isNearAvgStdCheckPercentageSmallValueThreshold = 0.1;
  public static final double absoluteSmallValueForAvg = 1;
  public static final float SMALL_STD_FACTOR = 2.5f;
  public static final float LARGE_STD_FACTOR = 5.0f;
  public static final float DEFAULT_IS_NEAR_AVG_STD_CHECK_PERCENTAGE = 1.7f;
  public static final float DEFAULT_PREDICTION_IS_NEAR_AVG_STD_CHECK_PERCENTAGE = 3.5f;
  public static final float LOW_IS_NEAR_AVG_STD_CHECK_PERCENTAGE = 1.6f;
  public static final float LOW_PREDICTION_IS_NEAR_AVG_STD_CHECK_PERCENTAGE = 3.0f;

  // anomaly consolidation
  // threshold checking
  public static final float overThresholdAnomalyRatio = 10.0f;
  public static final int OVER_THRESHOLD_ANOMALY_NID = -1;
  //pattern checking
  public static final int OVER_PATTERN_ANOMALY_NID = -5;
  public static final float PATTERN_DEFAULT_RATIO = 5.0f;
  //baseline checking
  public static final float DEFAULT_BASELINE_VIOLATION_FACTOR = 2;
  public static final int OVER_BASELINE_ANOMALY_NID = -6;
  public static final float BASELINE_DETECTION_MAX_SCORE = 1000.0f;
  public static final float MAX_ANOMALY_RATIO = 1000.0f;
  // constance zero checking
  public static final int OVER_CONSTANCE_ZERO_NID = -3;
  public static int DBScanAnomalyRatio = 1;

  // Change point detection parameters
  public static final int CONFIDENCE_LEVEL = 95;
  public static final int CHANGE_POINT_LEVEL = 10;
  public static final float OUTLIER_THRESHOLD = 1.1f;
  public static final float OUTLIER_FILTER = .1f;
  public static final float thresholdThreshold = 0.9f;
  public static final float thresholdbwThreshold = .984f;

  // FFT parameters
  public static final float FFT_CORRELATION_HIGH_SENSITIVITY = 0.5f;
  public static final float FFT_CORRELATION_MEDIUM_SENSITIVITY = 0.75f;
  public static final float FFT_CORRELATION_LOW_SENSITIVITY = 0.85f;
  public static final int dominatingFreqNum = 10;
  public static final float DEFAULT_FFT_THRESHOLD = FFT_CORRELATION_MEDIUM_SENSITIVITY;
  public static int maxCycleNumForSignature = 5;
  public static int MIN_FFT_WINDOW_APPFORECAST = 10;
  public static int MIN_FFT_WINDOW_LIVE = 3;
  public static int MIN_REPAETING_WINDOW_COUNT = 4;
  /*
   * for searching the exact window size
   */
  public static final int FFTWindowSearchRange = 10;
  public static final long FFTDataLen = 4 * 7 * 24 * 60l;// minutes, 4 weeks


  // free format parsing
  public static final int LOG_COLLECTION_MAX_RETRY_NUM = 10;
  // disable dropping unless all NaN
  public static final float minColumnDroppingNanPercentage = 0;

  public static final int Default_samplingInterval = 5;
  public static final int AWS_samplingInterval = 5; // minute
  public static final int MAX_MODEL_COUNT = 500;


  public static final String metricPredictionModelType = "Markov";
  public static final int SMALL_WINDOW_SIZE = 3;
  public static final int MEDIUM_WINDOW_SIZE = 5;
  public static final int LARGE_WINDOW_SIZE = 15;

  public static final boolean projectCronPredictionEnabled = true;
  public static final long predictSecondIntervalTrainingWindow = 256; // sample count
  public static final long PREDICT_LARGE_INSTANCE_TRAINING_WINDOW = 240; // sample count
  public static final int metricPredictionBinNum = 20;

  public static final String API_KEY = "apikey";
  public static final String APP_KEY = "appkey";
  public static final String KPI_METRICS = "kpiMetrics";

  public static final float MEAN_SQUARE_ERROR_DEFAULT = 0.1f;
  public static final int LAST_SLOPE_DATA_LENGTH = 8;

  // project settings
  public static final long projectModelPickingExpiry = 24 * 3600000l;

  // splunk Settings
  public static final String SPLUNK_EVENT_COLLECTOR_POSTFIX = "/services/collector";
  public static final String SPLUNK_BASE_TOKEN = "Splunk ";
  public static String HTTP_PREFIX = "http://";
  public static String HTTPS_PREFIX = "https://";

  // aws operation
  public static String awsOperationApi = "http://54.86.67.186:8080/aws/";
  public static String awsAccessKey = "AWS_ACCESS_KEY";
  public static String awsSecretKey = "AWS_SECRET_KEY";
  // project types
  public static final String CLOUD_TYPE_LLMEvaluation = "LLMEvaluation";
  public static final String CLOUD_TYPE_L2M = "LogToMetric";
  public static final String CLOUD_TYPE_LOG_FREQUENCY = "LogFrequency";
  public static final String CLOUD_TYPE_LOG_CDF = "LogCDF";
  public static final String CLOUD_TYPE_DATA_DOG = "DataDog";
  public static final String CLOUD_TYPE_DYNATRACE = "Dynatrace";

  public static final String CLOUD_TYPE_NAGIOS = "Nagios";
  public static final String CLOUD_TYPE_CLOUD_WATCH = "CloudWatch";
  public static final String CLOUD_TYPE_SERVICE_NOW = "ServiceNow";
  public static final String CLOUD_TYPE_PAGER_DUTY = "PagerDuty";
  public static final String CLOUD_TYPE_ZENDESK = "Zendesk";
  public static final String CLOUD_TYPE_SUMOLOGIC = "SumoLogic";
  public static final String CLOUD_TYPE_GOOGLE_PUBSUB = "GooglePubsub";
  public static final String CLOUD_TYPE_GOOGLE_ADS = "GoogleADs";
  public static final String CLOUD_TYPE_AMAZON_S3 = "AmazonS3";
  public static final String CLOUD_TYPE_GOOGLE_BIGQUERY = "GoogleBigQuery";
  public static final String CLOUD_TYPE_GOOGLE_CLOUD = "GoogleCloud";
  public static final String CLOUD_TYPE_SNOWFLAKE = "SnowFlake";
  public static final String CLOUD_TYPE_DATABRICKS = "DataBricks";
  public static final String CLOUD_TYPE_GOOGLE_CLOUD_COST = "GoogleCloudCost";
  public static final String CLOUD_TYPE_AWS_COST = "AWSCost";

  public static final String CLOUD_TYPE_AZURE_COST = "AzureCost";
  public static final String CLOUD_TYPE_AZURE = "AZURE";

  public static final String CLOUD_TYPE_KUBERNETES = "Kubernetes";
  public static final String CLOUD_TYPE_KUBERNETES_NODE = "KubernetesNode";
  public static final String CLOUD_TYPE_KUBERNETES_POD = "KubernetesPod";
  public static final String CLOUD_TYPE_KUBERNETES_EVENT = "KubernetesEvent";
  public static final String CLOUD_TYPE_NEW_RELIC = "NewRelic";
  public static final String CLOUD_TYPE_JDBC = "JDBC";
  public static String caName = "CaAPM";
  public static String signalFxName = "SignalFx";
  public static String DataDogName = "DataDog";
  public static String HadoopName = "Hadoop";
  public static final String PROJECT_TYPE_LOG_FILE = "LogFile";
  public static final String PROJECT_TYPE_HISTORICAL = "Historical";
  public static final String RABBITMQ_RERUN = "rerun";
  public static final String RABBITMQ_UPDATE = "update";
  public static final String RABBITMQ_DEDICATED = "dedicated";
  public static final String PROJECT_TYPE_STREAMING = "Custom";
  public static final String PROJECT_TYPE_CONTAINER_HISTORICAL = "ContainerHistorical";
  public static final String PROJECT_TYPE_CONTAINER_STREAMING = "ContainerCustom";

  // action triggering
  public static final int CUSTOM_ACTION_PORT = 4446;
  public static final int CUSTOM_ACTION_TIMEOUT = 1000;
  // Syscall triggering
  public static double EPSILON_STATS = 0.001;
  public static String minAnomalyRatioFilter = "10.0";

  // training data filling NaN
  public static String ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND";
  public static String ERROR_PROJECT_NOT_FOUND = "ERROR_PROJECT_NOT_FOUND";
  public static String ERROR_STATIONARY_PROJECT = "ERROR_STATIONARY_PROJECT";
  public static String ERROR_TRAINING_FAILED = "ERROR_TRAINING_FAILED";
  public static String SUCCESS = "SUCCESS";

  // log analysis
  public static final String ALL_STR_REGEX = ".*";
  // threshold to verify the current model is good to go or not
  public static final double BEST_LOG_MODEL_THRESHOLD = 0.7;
  // regex to check if a string is format of number
  public final static String NUMERIC_STR_REGEX = "(-)?\\d+(\\.\\d+)?";
  public final static String TOPK_STR_SEPARATOR = "[^a-zA-Z0-9]+";
  // regex to find the keywords to match in the query, it will match any string between two " "
  // e.g. for a string "asda"&"bas"||"asdas asdasd", it will match
  // "asda", "bas" and "asdas asdasd"
  public final static String KEYWORDS_QUERY_REGEX = "\"[^\"]+\"";
  // regex to find any substring is "&" and "||"
  public static final String KEYWORDS_CONDITION_REGEX = "\"\\s*(AND|OR)\\s*\"";
  // example: 4, anomaly ratio: 1.693281338686972, neuron Id: 384, Fault hints: 1.incorrect[ip-172-31-50-12][][](100.0)(35.71666717529297); 2.0x820e[ip-172-31-50-12][][](100.0)(71.15952627999442)

  // regex pattern
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
  public static String rawLogDataDirectoryName = "rawLogData";
  public static String logAnalysisRawdataFilename = "rawData.json";
  // For one chunk at most 500,000 length of string which is around 0.5MB for UTF-8
  public static final int LOG_EVENTS_CHUNK_SIZE = 500000;
  // Used to be "[^a-zA-Z0-9-._/:\'\"\\\\]", "\\W"
  public static String wordCountRegexSeparator = "[^a-zA-Z0-9-._]";
  // used to be "\\W" non word character "[\\s+.]"
  // anomaly string example "892(1)(-86.56716417910448)" separator: "(", ")(", ")"
  // Log email threshold
  // Log event table threshold
  // Formula for hot/cold event: (frequency + 100) * baseScore; Hot/Cold score factor 0.25/0.2
  // For rare events the score is always 75
  // base score for rare/hot/cold event
  public static final int CRITICAL_EVENT_ID = 5;
  public static final double MAX_LOG_ANOMALY_SCORE = 1000;
  public static final String EVENT_ARRAY = "eventArray";
  public static final String EVENTS = "events";
  public static final double DEFAULT_SCORE = 0.0;
  // 0.5MB
  public static final int MAX_BLOCK_SIZE = 500000;

  // event relationship
  public static final int INTER_AND_INTRA = 0;
  public static final int ONLY_INTER = 1;
  public static final int ONLY_INTRA = 2;
  public static final int INTRA_FIRST = 0;
  public static final int INTER_FIRST = 1;
  public static final int COMBINED = 2;
  public static final String KPI_RESULT = "kpiResult";
  public static final String ALL_GROUP = "All";
  public static final long defaultEventRelationshipCreateInterval = 2 * ONE_DAY_IN_MILLIS;
  public static final int DEFAULT_INCIDENT_RELATION_SEARCH_HOUR = 6;
  public static final int DEFAULT_MAX_INCIDENT_PREDICTION_WINDOW_HOUR = 12;
  public static final int DEFAULT_MIN_INCIDENT_PREDICTION_WINDOW_MIN = 5;
  public static final Integer BACKUP_CAUSAL_LOG_PREDICTION_COUNT_THRESHOLD = 1;
  public static final Float BACKUP_CAUSAL_LOG_PREDICTION_PROBABILITY_THRESHOLD = 0.8f;

  public static final Double DEFAULT_CAUSAL_PROBABILITY_IN_PAGE = 0.5;
  public static final long DEFAULT_PREDICTION_ROOTCAUSE_VALID_RANGE =
      4 * GlobalConstant.ONE_HOUR_IN_MILLIS;
  public static final int DEFAULT_INCIDENT_PREDICTION_EVENT_NUMBER_LIMIT = 50;
  public static final long DEFAULT_INCIDENT_PREDICTION_EVENT_CONSOLIDATION_WINDOW = GlobalConstant.ONE_HOUR_IN_MILLIS;
  public static final Integer defaultEventRelationshipMergeNum = 1;// merge 4 sample interval to one
  public static final Long CORRELATION_VALID_INTERVAL_THRESHOLD = ONE_MINUTE;
  public static final long defaultEventRelationshipMergeInterval = 60000L;// merge 4 sample interval
  public static final boolean REVERSE_SEARCH = true;
  // to one
  public static final String EVENT_RELATION_INITIALIZING_STATUS = "initializing";
  public static final String EVENT_RELATION_CREATING_STATUS = "creating";
  public static final String EVENT_RELATION_FINISH_STATUS = "finished";
  public static final String EVENT_RELATION_FAIL_STATUS = "Fail";
  public static final String EVENT_RELATION_TIMEOUT_STATUS = "Timeout";
  public static final String EVENT_RELATION_FINISH_INTER_STATUS = "inter part finish";
  public static final String EVENT_RELATION_EMPTY_RESULT_STATUS = "EmptyResult";
  public static final String EVENT_RELATION_DELETE_STATUS = "Deleting";
  public static final Integer MI_INIT_STATUS = 0;
  public static final Integer MI_CREATING_STATUS = 1;
  public static final Integer MI_FINISH_STATUS = 2;
  public static final Integer MI_RELATION_FAIL_STATUS = 3;
  public static final Integer MI_RELATION_TIMEOUT_STATUS = 4;
  public static final Integer MI_RELATION_EMPTY_STATUS = 5;
  public static final Integer MI_RELATION_DELETE_STATUS = 6;
  public static final int MULTI_HOP_TRACE_LIMIT = 2;
  public static final int MULTI_HOP_RESULT_LIMIT = 5;
  public static final int RCA_EMAIL_ROOT_CAUSE_RESULT_LIMIT = 3;
  public static final int DEFAULT_MULTI_HOP_SEARCH_LIMIT = 30;
  public static final int DEFAULT_ROOT_CAUSE_LOG_MESSAGE_SEARCH_RANGE = 240;
  // email alert
  public static final long emailAlertCoolDownWindow = ONE_HOUR_IN_MILLIS;

  public static final String CUSTOMER_NAME = "customerName";
  public static final String ENVIRONMENT_NAME = "environmentName";
  public static final String SYSTEM_NAME = "systemName";
  public static final String PROJECT_DATA_TYPE = "dataType";
  public static final String DATA_TYPE_ALL = "all";
  public static final String DATA_TYPE_SERVICE_MAP = "serviceMap";
  public static final String ZONE_LIST = "zoneList";

  public static final String DATA_TYPE_ANOMALY = "anomaly";
  public static final String DATA_TYPE_METRIC = "Metric";
  public static final String DATA_TYPE_LOG = "Log";
  public static final String DATA_TYPE_INCIDENT = "Incident";
  public static final String DATA_TYPE_DEPLOYMENT = "Deployment";
  public static final String DATATYPE_LOGDERIVED = "LogDerived";
  public static final String DATA_TYPE_ALERT = "Alert";
  public static final String DATA_TYPE_TRACE = "Trace";
  public static final String POSTFIX_TRACE_PROMPT = "-Prompt";
  public static final String DATA_TYPE_NORMAL_ALERT = "normal alert";
  public static final String DATA_TYPE_METRIC_ANOMALY = "MetricAnomaly";
  public static final String DATA_TYPE_LOG_ANOMALY = "LogAnomaly";
  public static final String KPI_INCIDENT_TYPE = "KPI Incident";

  public static final boolean logDerivedHolisticFlag = true;
  public static final String MODEL_TYPE = "modelType";
  public static final String MODELKEYTYPE_HOLISTICMETRIC = "holisticMetric";
  public static final String MODELKEYTYPE_HOLISTIC = "Holistic";
  public static final String MODELKEYTYPE_SPLITBYINSTANCELOG = "splitByInstanceLog";
  public static final String MODELKEYTYPE_SPLITBYINSTANCE = "splitByInstance";
  public static final String MODELKEYTYPE_SPLITBYGROUP = "splitByGroup";
  public static final String MODELKEYTYPE_SPLITBYSERVICE = "splitByService";
  public static final String MODELKEYTYPE_SPLITBYENV = "splitByEnv";
  public static final String MODELKEYTYPE_HYBRID = "Hybrid";
  public static final String MODELKEYTYPE_DBSCAN = "DBScan";
  public static final String MODELKEYTYPE_THRESHOLD = "Threshold";
  public static String MODELKEYTYPE_LOGDERIVED =
      logDerivedHolisticFlag ? "holisticLogDerived" : "splitLogDerived";

  public static final String DETECTED_INCIDENT = "Detected Incident";
  public static final String PREDICTED_INCIDENT = "Predicted Incident";

  public static final String DETECTED_ANOMALY = "Detected Anomaly";
  public static final String MANUALLY_TRIGGERED = "Manually Triggered";
  public static final String TIMELINE = "timeline";
  public static final String CHAIN = "chain";
  // column header adding serviceName and envName
  // cron intervals
  public static final boolean enforceCronCollectInterval = true;

  public static final String logFileMetricPrefix = "metric";

  public static final String DEFAULT_COLD_EVENT_NUMBER = "0";
  public static final String DEFAULT_RARE_EVENT_NUMBER = "20";
  public static final String DEFAULT_HOT_EVENT_NUMBER = "20";
  public static final String DEFAULT_WHITELIST_EVENT_NUMBER = "20";
  public static final String DEFAULT_NEW_PATTERN_EVENT_NUMBER = "20";
  public static final int DEFAULT_METRIC_C_VALUE_SETTING = 6;
  public static final int DEFAULT_METRIC_HIGH_RATIO_C_VALUE_SETTING = 3;
  public static final Float LOW_P_VALUE = 0.99F;
  public static final Float MEDIUM_LOW_P_VALUE = 0.95F;
  public static final Float MEDIUM_P_VALUE = 0.9F;
  public static final Float MEDIUM_HIGH_P_VALUE = 0.75F;
  public static final Float HIGH_P_VALUE = 0.5F;
  public static final Float LOW_P_VALUE_NEAR_CONSTANCE_RATIO = 0.001F;
  public static final Float MEDIUM_LOW_P_VALUE_NEAR_CONSTANCE_RATIO = 0.01F;
  public static final Float MEDIUM_P_VALUE_NEAR_CONSTANCE_RATIO = 0.025F;
  public static final Float MEDIUM_HIGH_P_VALUE_NEAR_CONSTANCE_RATIO = 0.05F;
  public static final Float HIGH_P_VALUE_NEAR_CONSTANCE_RATIO = 0.1F;
  public static final Float LOW_P_VALUE_NEAR_CONSTANCE_VALUE = 5.0F;
  public static final Float MEDIUM_LOW_P_VALUE_NEAR_CONSTANCE_VALUE = 2.0F;
  public static final Float MEDIUM_P_VALUE_NEAR_CONSTANCE_VALUE = 1.5F;
  public static final Float MEDIUM_HIGH_P_VALUE_NEAR_CONSTANCE_VALUE = 1.0F;
  public static final Float HIGH_P_VALUE_NEAR_CONSTANCE_VALUE = 0.1F;
  public static final String DEFAULT_P_VALUE = MEDIUM_P_VALUE.toString();
  public static final String DEFAULT_C_VALUE = "1";
  public static final String defaultLogCValue = "1";

  //evaluation and satisfaction
  public static final Integer MAX_SATISFACTION_SCORE = 5;
  public static final Integer DEFAULT_SATISFACTION_SCORE = 4;
  public static final Float FEEDBACK_PERCENTAGE_OF_SATISFACTION = 0.8F;

  public static class MetricUnit {

    public double multiplier;
    public String unitString;
    public String unitOnlyString;
    public int groupId;
    public String prettyName;

    public MetricUnit(String unit, String pname, double mult) {
      multiplier = mult;
      unitString = unit;
      prettyName = pname;
      if (unitString.indexOf("(") != -1) {
        unitOnlyString = unitString.split("[()]")[1];
      } else {
        unitOnlyString = "";
      }
    }
  }


  // cpu metrics
  public static List<String> cpuMetrics;
  public static List<String> memMetrics;
  public static double logDerivedMatchPercentage = 0.7;
  public static double logMatchPercentage = 0.3;
  public static double metricMatchPercentage = 0.8;
  public static long METRIC_VALID_MODEL_SPAN_DURATION = 4 * ONE_HOUR_IN_MILLIS;
  public static int episodeMaxLength = 6;
  public static long modelPickingSearchDays = 7;

  public static long sendSplunkDataTimeOut = 1000 * 60 * 2; // 2 min timeout for send log email

  public static int markovAvgLen = 5;


  public static final long LOG_RECEIVE_LOCK_TIMEOUT = GlobalConstant.ONE_MINUTE * 5;
  public static final String LOG_RECEIVE_LOCK_NAME = "logReceive";
  public static final String LOG_TRAINING_LOCKER_NAME = "logtraining";
  public static String logSendSplunkDataLockerName = "logSendSplunkData";
  public static long LOG_TRAINING_LOCKER_TIMEOUT = 3 * 60 * 60 * 1000l;

  static {
    cpuMetrics = new ArrayList<>();
    cpuMetrics.add("CPU");
    cpuMetrics.add("CPUUtilization");
    cpuMetrics.add("CPU_utilization");
    cpuMetrics.add("cpuUsed");
    cpuMetrics.add("cpu-usedPercent.avg");
    cpuMetrics.add("ProcessCpuLoad");
    cpuMetrics.add("cpu.system");
    cpuMetrics.add("CPUUserTime");
    memMetrics = new ArrayList<>();
    memMetrics.add("MemUsed");
  }

  // special model type
  public static final String generalLogWorker = "GLW";
  public static final String generalWorker = "GW";
  public static final String logReducer = "LR";
  public static final String dedicatedLogReceiving = "DLR";
  public static final String dedicatedAgentReceiving = "DAR";
  // RabbitMQ general log reducer critical queues
  public static final Set<String> rabbitMQLogReducerCriticalQueues = new HashSet<String>();

  static {
    rabbitMQLogReducerCriticalQueues.add("detect-log-reducer-queue");
  }

  // RabbitMQ general log worker critical queues
  public static final Set<String> rabbitMQGeneralLogWorkerCriticalQueues = new HashSet<String>();

  static {
    rabbitMQGeneralLogWorkerCriticalQueues.add("detect-log-collect-queue");
  }

  // RabbitMQ general worker critical queues
  public static final Set<String> rabbitMQGeneralWorkerCriticalQueues = new HashSet<String>();

  static {
    rabbitMQGeneralWorkerCriticalQueues.add("detect-log-pull-queue");
    rabbitMQGeneralWorkerCriticalQueues.add("detect-pull-queue");
  }

  // RabbitMQ dedicated agent worker critical queues
  public static final Set<String> rabbitMQDedicatedAgentWorkerCriticalQueues = new HashSet<String>();

  static {
    rabbitMQDedicatedAgentWorkerCriticalQueues.add("CustomProjectData-queue0");
    rabbitMQDedicatedAgentWorkerCriticalQueues.add("CustomProjectData-queue1");
    rabbitMQDedicatedAgentWorkerCriticalQueues.add("CustomProjectData-queue2");
    rabbitMQDedicatedAgentWorkerCriticalQueues.add("CustomProjectData-queue3");
  }

  // RabbitMQ dedicated log worker critical queues
  public static final Set<String> rabbitMQDedicatedLogWorkerCriticalQueues = new HashSet<String>();

  static {
    rabbitMQDedicatedLogWorkerCriticalQueues.add("TDLogStreaming-queue0");
    rabbitMQDedicatedLogWorkerCriticalQueues.add("TDLogStreaming-queue1");
  }

  // root cause
  public static final Map<String, String> ROOT_CAUSES_TYPE_MAP;
  public static final String TRAILING = "trailing";
  public static final int MAXIMUM_ROOT_CAUSE_SIZE = 5;

  static {
    ROOT_CAUSES_TYPE_MAP = new HashMap<>();
    ROOT_CAUSES_TYPE_MAP.put("missing", "missing metric data");
    ROOT_CAUSES_TYPE_MAP.put("cpu", "CPU usage increase");
    ROOT_CAUSES_TYPE_MAP.put("mem", "memory usage increase");
    ROOT_CAUSES_TYPE_MAP.put("disk", "disk contention");
    ROOT_CAUSES_TYPE_MAP.put("fs", "file system activity increase");
    ROOT_CAUSES_TYPE_MAP.put("network", "network traffic surge");
  }

  public static final Map<String, String> SUGGESTION_CATEGORY_MAP;

  static {
    SUGGESTION_CATEGORY_MAP = new HashMap<>();
    SUGGESTION_CATEGORY_MAP.put("missing", "missing metric data");
    SUGGESTION_CATEGORY_MAP.put("cpu", "CPU usage increase");
    SUGGESTION_CATEGORY_MAP.put("mem", "memory usage increase");
    SUGGESTION_CATEGORY_MAP.put("disk", "disk contention");
    SUGGESTION_CATEGORY_MAP.put("fs", "file system activity increase");
    SUGGESTION_CATEGORY_MAP.put("network", "network traffic surge");
    SUGGESTION_CATEGORY_MAP.put("instanceDown", "instance down");
  }

  public static final Map<String, String> rootCauseConstanceZeroMap;

  static {
    rootCauseConstanceZeroMap = new HashMap<>();
    rootCauseConstanceZeroMap.put("missing", "missing metric data");
    rootCauseConstanceZeroMap.put("cpu", "CPU usage sudden zero");
    rootCauseConstanceZeroMap.put("mem", "memory usage sudden zero");
    rootCauseConstanceZeroMap.put("disk", "disk usage sudden zero");
    rootCauseConstanceZeroMap.put("fs", "file system activity sudden zero");
    rootCauseConstanceZeroMap.put("network", "network traffic sudden zero");
  }

  public static final Map<String, String> rootCauseTypesNegativeMap;

  static {
    rootCauseTypesNegativeMap = new HashMap<>();
    rootCauseTypesNegativeMap.put("missing", "missing metric data");
    rootCauseTypesNegativeMap.put("cpu", "CPU usage reduction");
    rootCauseTypesNegativeMap.put("mem", "memory usage reduction");
    rootCauseTypesNegativeMap.put("disk", "disk usage reduction");
    rootCauseTypesNegativeMap.put("fs", "low file system usage");
    rootCauseTypesNegativeMap.put("network", "network traffic reduction");
  }

  public static final Map<String, String> suggestedActionMap;

  static {
    suggestedActionMap = new HashMap<>();
    suggestedActionMap.put("missing", "check data source");
    suggestedActionMap.put("cpu", "check CPU");
    suggestedActionMap.put("mem", "check memory");
    suggestedActionMap.put("disk", "check disk");
    suggestedActionMap.put("fs", "check file system");
    suggestedActionMap.put("network", "check network");
  }

  public static final HashMap<String, String> criticalThresholdAnomalies;

  static {
    criticalThresholdAnomalies = new HashMap<>();
    criticalThresholdAnomalies.put("DiskUsed+higher", "Disk full");
    criticalThresholdAnomalies.put("CPU+higher", "CPU saturated");
    criticalThresholdAnomalies.put("MemUsed+higher", "Out of Memory");
    criticalThresholdAnomalies.put("CPU+lower", "Idle node");
  }

  // Storage clients
  public static final int cassandraClientBufferSize = 2 * 1024 * 1024;
  public static final String CASSANDRA_DATA_STORE_KEYSPACE = "insightfinderspace";
  public static final String CASSANDRA_CLIENT_KEYSPACE = "insightfinderstorage";

  // Datastore client
  public static final String CASSANDRASTORE = "CASSANDRASTORE";
  // role for user
  public static final String ADMIN_USER_ROLE = "Admin";
  public static final String LOCAL_ADMIN_USER_ROLE = "LocalAdmin";
  public static final String NORMAL_USER_ROLE = "NormalUser";
  public static final String READ_ONLY_USER_ROLE = "ReadOnlyUser";
  // filtering out transient metric (not in list) by assigning :I suffix
  public static final Map<String, List<String>> agentMetricWhitelistMap;
  public static final Map<String, String> agentMetricIncludeMap;
  public static final Map<String, String> agentMetricExcludeMap;

  static {
    agentMetricWhitelistMap = new HashMap<>();
    agentMetricIncludeMap = new HashMap<>();
    agentMetricExcludeMap = new HashMap<>();

    agentMetricExcludeMap.put("hypervisor", ":8000");
    agentMetricIncludeMap.put("hypervisor", ":8001");
    agentMetricWhitelistMap.put("hypervisor",
        Arrays.asList("timestamp", "cpuUsed", "cpuSystem", "cpuOverlap", "cpuRun", "cpuReady",
            "cpuWait", "DiskCommands", "DiskReadCommands", "DiskWriteCommands", "DiskReadRate",
            "DiskWriteRate", "MemUsed", "NetworkIn/vSwitch0Total", "NetworkOut/vSwitch0Total",
            "NetworkIn/vSwitch1Total", "NetworkOut/vSwitch1Total"));

    agentMetricExcludeMap.put("proc", ":1000");
    agentMetricIncludeMap.put("proc", ":1001");
    agentMetricWhitelistMap.put("proc",
        Arrays.asList("timestamp", "CPU", "DiskRead", "DiskWrite", "DiskUsed", "MemUsed",
            "NetworkIn", "NetworkOut", "LoadAvg1", "LoadAvg5", "LoadAvg15", "MemTotal", "SharedMem",
            "SwapUsed", "SwapTotal", "InOctets", "OutOctets", "InErrors", "OutErrors", "InDiscards",
            "OutDiscards"));

    agentMetricExcludeMap.put("elasticsearch", ":6000");
    agentMetricIncludeMap.put("elasticsearch", ":6001");
    agentMetricWhitelistMap.put("elasticsearch",
        Arrays.asList("timestamp", "NumberOfNodes", "NumberOfDataNodes",
            "NumberOfActivePrimaryShards", "NumberOfActiveShards", "NumberOfRelocatingShards",
            "NumberOfInitializingShards", "NumberOfUnAssignedShards", "TotalGetRequests",
            "TotalIndexRequestsPerNode", "TotalMerges", "TotalQueryTime",
            "TotalSearchRequestsPerNode", "GarbageCollectorTime", "NumberOfDocumentsInNode",
            "NumberOfDeletedDocuments", "CurrentMerges", "NumberOfSegments", "CommittedHeap",
            "UsedHeap", "NumberOfOpenFileDescriptors", "ProcessCpuPercent", "RejectedBulkRequests",
            "RejectedFlushRequests", "RejectedGenericRequests", "RejectedGetRequests",
            "RejectedIndexRequests", "RejectedForceMergeRequests", "RejectedRefreshRequests",
            "RejectedSearchRequests", "RejectedSnapshotRequests", "TotalSearchRequestsPerCluster",
            "TotalIndexRequestsPerCluster", "FieldDataSize", "NumberOfMerges",
            "NumberOfDocumentsInCluster"));

  }

  // metric unit maps
  public static final Map<String, MetricUnit> awsMetricWhitelist;
  public static final Map<String, MetricUnit> ec2MetricWhitelist;
  public static final Map<String, MetricUnit> dynamoDBMetricWhitelist;
  public static final Map<String, MetricUnit> lambdaMetricWhitelist;
  public static final Map<String, MetricUnit> rdsMetricWhitelist;
  public static final Map<String, MetricUnit> rdsproxyMetricWhitelist;
  public static final Map<String, MetricUnit> gcpMetricWhitelist;
  public static final Map<String, MetricUnit> newRelicMetricWhitelist;
  public static final Map<String, MetricUnit> customMetricWhitelist;
  public static final Map<String, MetricUnit> allMetricWhitelist;
  public static final HashMap<String, Integer> caMetricMetaMap;
  public static final double H = 0;


  // OpenTSDB settings
  public static final String OpentsdbHostIp = "54.90.87.220";
  public static final int OpentsdbHostPort = 4242;


  static {
    ec2MetricWhitelist = new HashMap<>();
    dynamoDBMetricWhitelist = new HashMap<>();
    rdsMetricWhitelist = new HashMap<>();
    rdsproxyMetricWhitelist = new HashMap<>();
    lambdaMetricWhitelist = new HashMap<>();
    caMetricMetaMap = new HashMap<>();
    caMetricMetaMap.put("Health:Concurrent Invocations", 9090);
    caMetricMetaMap.put("Health:Average Response Time \\\\(ms\\\\)", 9091);
    caMetricMetaMap.put("Health:Errors Per Interval", 9092);
    caMetricMetaMap.put("Health:Responses Per Interval", 9093);
    caMetricMetaMap.put("Health:Stall Count", 9094);
    caMetricMetaMap.put("GC Heap:Bytes In Use", 9095);
    ec2MetricWhitelist.put("CPUUtilization", new MetricUnit("CPU (%)", "CPU", 1.0));
    ec2MetricWhitelist.put("DiskReadOps", new MetricUnit("Disk (# of Ops)", "disk", 1.0));
    ec2MetricWhitelist.put("DiskWriteOps", new MetricUnit("Disk (# of Ops)", "disk", 1.0));
    ec2MetricWhitelist.put("NetworkIn",
        new MetricUnit("Network (MB)", "network", 1.0 / 1024 / 1024));
    ec2MetricWhitelist.put("NetworkOut",
        new MetricUnit("Network (MB)", "network", 1.0 / 1024 / 1024));
    ec2MetricWhitelist.put("DiskReadBytes", new MetricUnit("Disk (MB)", "disk", 1.0 / 1024 / 1024));
    ec2MetricWhitelist.put("DiskWriteBytes",
        new MetricUnit("Disk (MB)", "disk", 1.0 / 1024 / 1024));
    ec2MetricWhitelist.put("NetworkPacketsIn",
        new MetricUnit("Network Packets In (Count)", "network", 1.0));
    ec2MetricWhitelist.put("NetworkPacketsOut",
        new MetricUnit("Network Packets Out (Count)", "network", 1.0));
    // ec2MetricWhitelist.put("StatusCheckFailed", new MetricUnit(
    // "StatusCheckFailed_Any (Count)", 1.0));
    // ec2MetricWhitelist.put("CPUCreditUsage", new MetricUnit(
    // "CPUCreditUsage (Count)", 1.0));
    // ec2MetricWhitelist.put("CPUCreditBalance", new MetricUnit(
    // "CPUCreditBalance (Count)", 1.0));

    rdsMetricWhitelist.put("CPUUtilization", new MetricUnit("CPU (%)", "CPU", 1.0));
    rdsMetricWhitelist.put("DatabaseConnections",
        new MetricUnit("DB Connections (Count)", "DB load", 1.0));
    rdsMetricWhitelist.put("ReadIOPS",
        new MetricUnit("Read Operations (Count/Second)", "DB load", 1.0));
    rdsMetricWhitelist.put("WriteIOPS",
        new MetricUnit("Write Operations (Count/Second)", "DB load", 1.0));
    rdsMetricWhitelist.put("ReadLatency",
        new MetricUnit("Read Latency (ms)", "DB load", 1.0 * 1000));
    rdsMetricWhitelist.put("WriteLatency",
        new MetricUnit("Write Latency (ms)", "DB load", 1.0 * 1000));
    rdsMetricWhitelist.put("ReadThroughput",
        new MetricUnit("Read Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsMetricWhitelist.put("WriteThroughput",
        new MetricUnit("Write Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsMetricWhitelist.put("NetworkReceiveThroughput",
        new MetricUnit("Network Receive Throughput (MB/Second)", "network", 1.0 / 1024 / 1024));
    rdsMetricWhitelist.put("NetworkTransmitThroughput",
        new MetricUnit("Network Transmit Throughput (MB/Second)", "network", 1.0 / 1024 / 1024));

    rdsproxyMetricWhitelist.put("AvailabilityPercentage", new MetricUnit("CPU (%)", "CPU", 1.0));
    rdsproxyMetricWhitelist.put("ClientConnections",
        new MetricUnit("DB Connections (Count)", "DB load", 1.0));
    rdsproxyMetricWhitelist.put("ClientConnectionsClosed",
        new MetricUnit("Read Operations (Count/Second)", "DB load", 1.0));
    rdsproxyMetricWhitelist.put("ClientConnectionsNoTLS",
        new MetricUnit("Write Operations (Count/Second)", "DB load", 1.0));
    rdsproxyMetricWhitelist.put("ClientConnectionsReceived",
        new MetricUnit("Read Latency (ms)", "DB load", 1.0 * 1000));
    rdsproxyMetricWhitelist.put("ClientConnectionsSetupFailedAuth",
        new MetricUnit("Write Latency (ms)", "DB load", 1.0 * 1000));
    rdsproxyMetricWhitelist.put("ClientConnectionsSetupSucceeded",
        new MetricUnit("Read Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("ClientConnectionsTLS",
        new MetricUnit("Write Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("DatabaseConnectionRequests",
        new MetricUnit("Network Receive Throughput (MB/Second)", "network", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("DatabaseConnectionRequestsWithTLS",
        new MetricUnit("Network Transmit Throughput (MB/Second)", "network", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("DatabaseConnections",
        new MetricUnit("Write Operations (Count/Second)", "DB load", 1.0));
    rdsproxyMetricWhitelist.put("DatabaseConnectionsBorrowLatency",
        new MetricUnit("Read Latency (ms)", "DB load", 1.0 * 1000));
    rdsproxyMetricWhitelist.put("DatabaseConnectionsCurrentlyBorrowed",
        new MetricUnit("Write Latency (ms)", "DB load", 1.0 * 1000));
    rdsproxyMetricWhitelist.put("DatabaseConnectionsCurrentlyInTransaction",
        new MetricUnit("Read Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("DatabaseConnectionsCurrentlySessionPinned",
        new MetricUnit("Write Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("DatabaseConnectionsSetupFailed",
        new MetricUnit("Network Receive Throughput (MB/Second)", "network", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("DatabaseConnectionsSetupSucceeded",
        new MetricUnit("Network Transmit Throughput (MB/Second)", "network", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("DatabaseConnectionsWithTLS",
        new MetricUnit("Read Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("MaxDatabaseConnectionsAllowed",
        new MetricUnit("Write Throughput (MB/Second)", "DB load", 1.0 * 1000));
    rdsproxyMetricWhitelist.put("QueryDatabaseResponseLatency",
        new MetricUnit("Network Receive Throughput (MB/Second)", "network", 1.0 * 1000));
    rdsproxyMetricWhitelist.put("QueryRequests",
        new MetricUnit("Network Transmit Throughput (MB/Second)", "network", 1.0 * 1000));
    rdsproxyMetricWhitelist.put("QueryRequestsNoTLS",
        new MetricUnit("Read Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("QueryRequestsTLS",
        new MetricUnit("Write Throughput (MB/Second)", "DB load", 1.0 / 1024 / 1024));
    rdsproxyMetricWhitelist.put("QueryResponseLatency",
        new MetricUnit("Network Receive Throughput (MB/Second)", "network", 1.0 * 1000));

    dynamoDBMetricWhitelist.put("ProvisionedWriteCapacityUnits",
        new MetricUnit("Provisioned Write Capacity Units (Count)", "DB load", 1.0));
    dynamoDBMetricWhitelist.put("ProvisionedReadCapacityUnits",
        new MetricUnit("Provisioned Read Capacity Units (Count)", "DB load", 1.0));
    dynamoDBMetricWhitelist.put("ConsumedWriteCapacityUnits",
        new MetricUnit("Consumed Write Capacity Units (Count)", "DB load", 1.0));
    dynamoDBMetricWhitelist.put("ConsumedReadCapacityUnits",
        new MetricUnit("Consumed Read Capacity Units (Count)", "DB load", 1.0));

    lambdaMetricWhitelist.put("Invocations", new MetricUnit("Invocations (Count)", "DB load", 1.0));
    lambdaMetricWhitelist.put("Errors", new MetricUnit("Errors (Count)", "DB load", 1.0));
    lambdaMetricWhitelist.put("Dead Letter Error",
        new MetricUnit("Dead Letter Error (Count)", "DB load", 1.0));
    lambdaMetricWhitelist.put("Duration", new MetricUnit("Duration (Count)", "DB load", 1.0));
    lambdaMetricWhitelist.put("Throttles",
        new MetricUnit("Provisioned Write Capacity Units (Count)", "DB load", 1.0));
    lambdaMetricWhitelist.put("IteratorAge",
        new MetricUnit("Provisioned Read Capacity Units (Count)", "DB load", 1.0));
    lambdaMetricWhitelist.put("ConcurrentExecutions",
        new MetricUnit("Consumed Write Capacity Units (Count)", "DB load", 1.0));
    lambdaMetricWhitelist.put("UnreservedConcurrentExecutions",
        new MetricUnit("Consumed Read Capacity Units (Count)", "DB load", 1.0));

    // block all irrelevant metrics to reduce cloud watch api count
    // awsMetricWhitelist.put("BinLogDiskUsage", new MetricUnit(
    // "Binary Log Disk Usage (MB)", 1.0 / 1024 / 1024));
    // awsMetricWhitelist.put("DiskQueueDepth", new MetricUnit(
    // "Queue Depth (Count)", 1.0));
    // awsMetricWhitelist.put("FreeableMemory", new MetricUnit(
    // "Freeable Memory (MB)", 1.0 / 1024 / 1024));
    // awsMetricWhitelist.put("FreeStorageSpace", new MetricUnit(
    // "Free Storage Space (MB)", 1.0 / 1024 / 1024));
    // awsMetricWhitelist.put("ReplicaLag", new
    // MetricUnit("Replica Lag (ms)",
    // 1.0 * 1000));
    // awsMetricWhitelist.put("SwapUsage", new MetricUnit("Swap Usage (MB)",
    // 1.0 / 1024 / 1024));
    // awsMetricWhitelist
    // .put("ConditionalCheckFailedRequests", new MetricUnit(
    // "Conditional Check Failed Requests (Count)", 1.0));
    // awsMetricWhitelist.put("OnlineIndexConsumedWriteCapacity",
    // new MetricUnit("Online Index Consumed Write Capacity (Count)",
    // 1.0));
    // awsMetricWhitelist.put("OnlineIndexPercentageProgress", new
    // MetricUnit(
    // "Online Index Percentage Progress (Count)", 1.0));
    // awsMetricWhitelist.put("OnlineIndexThrottleEvents", new MetricUnit(
    // "Online Index Throttle Events (Count)", 1.0));
    // awsMetricWhitelist.put("ReadThrottleEvents", new MetricUnit(
    // "Read Throttle Events (Count)", 1.0));
    // awsMetricWhitelist.put("ReturnedBytes", new MetricUnit(
    // "Returned Bytes (MB)", 1.0 / 1024 / 1024));
    // awsMetricWhitelist.put("ReturnedItemCount", new MetricUnit(
    // "Returned Item Count (Count)", 1.0));
    // awsMetricWhitelist.put("ReturnedRecordsCount", new MetricUnit(
    // "Returned Records Count (Count)", 1.0));
    // awsMetricWhitelist.put("SuccessfulRequestLatency", new MetricUnit(
    // "Successful Request Latency (ms)", 1.0));
    // awsMetricWhitelist.put("SystemErrors", new MetricUnit(
    // "System Errors (count)", 1.0));
    // awsMetricWhitelist.put("ThrottledRequests", new MetricUnit(
    // "Throttled Requests (Count)", 1.0));
    // awsMetricWhitelist.put("UserErrors", new MetricUnit(
    // "User Errors (Count)", 1.0));
    // awsMetricWhitelist.put("WriteThrottleEvents", new MetricUnit(
    // "Write Throttle Events (Count)", 1.0));

    awsMetricWhitelist = new HashMap<String, MetricUnit>();
    awsMetricWhitelist.putAll(ec2MetricWhitelist);
    awsMetricWhitelist.putAll(rdsMetricWhitelist);
    awsMetricWhitelist.putAll(dynamoDBMetricWhitelist);
    newRelicMetricWhitelist = new HashMap<String, MetricUnit>();
    newRelicMetricWhitelist.put("CPUUserTime", new MetricUnit("CPU (%)", "CPU", 1.0));
    newRelicMetricWhitelist.put("CPUUtilization", new MetricUnit("CPU (%)", "CPU", 1.0));
    newRelicMetricWhitelist.put("HeapMemoryUsed", new MetricUnit("Memory (MB)", "memory", 1.0));
    newRelicMetricWhitelist.put("PhysicalMemoryUsed", new MetricUnit("Memory (MB)", "memory", 1.0));
    newRelicMetricWhitelist.put("InstanceconnectsReqPerMin",
        new MetricUnit("Memcache (# of Ops)", "Memcache", 1.0));
    newRelicMetricWhitelist.put("OtherTransactionMinRespTime",
        new MetricUnit("Response Time (ms)", "AppSvrRespTime", 1.0));
    newRelicMetricWhitelist.put("OtherTransactionMaxRespTime",
        new MetricUnit("Response Time (ms)", "AppSvrRespTime", 1.0));
    newRelicMetricWhitelist.put("OtherTransactionAvgRespTime",
        new MetricUnit("Response Time (ms)", "AppSvrRespTime", 1.0));
    newRelicMetricWhitelist.put("ControllerAvgRespTime",
        new MetricUnit("Response Time (ms)", "AppSvrRespTime", 1.0));
    newRelicMetricWhitelist.put("ControllerCallPerMin", new MetricUnit("count", "call", 1.0));
    newRelicMetricWhitelist.put("ControllerCallCount", new MetricUnit("count", "call", 1.0));
    newRelicMetricWhitelist.put("ControllerMinRespTime",
        new MetricUnit("Response Time (ms)", "AppSvrRespTime", 1.0));
    newRelicMetricWhitelist.put("ControllerMaxRespTime",
        new MetricUnit("Response Time (ms)", "AppSvrRespTime", 1.0));
    newRelicMetricWhitelist.put("ControllerAvgExclusiveTime",
        new MetricUnit("Avg ExclusiveTime (ms)", "AppSvrAvgExclusiveTime", 1.0));
    // newRelicMetricWhitelist.put("ControllerAvgValue",
    // new MetricUnit("Avg Value", "AppSvrAvgValue", 1.0));
    newRelicMetricWhitelist.put("ControllerTotalCallTimePerMin",
        new MetricUnit("Call Time (ms)", "AppSvrCallTime", 1.0));
    newRelicMetricWhitelist.put("ControllerReqPerMin",
        new MetricUnit("Req Per Min (# of Req)", "AppSvrReqPerMin", 1.0));
    // newRelicMetricWhitelist.put("ControllerStandardDeviation",
    // new MetricUnit("StandardDeviation", "AppSvrStandardDeviation", 1.0));
    // newRelicMetricWhitelist.put("ControllerThroughput",
    // new MetricUnit("Throughput", "AppSvrThroughput", 1.0));
    newRelicMetricWhitelist.put("ControllerAvgCallTime",
        new MetricUnit("Call Time (ms)", "AppSvrCallTime", 1.0));
    newRelicMetricWhitelist.put("ControllerMinCallTime",
        new MetricUnit("Call Time (ms)", "AppSvrCallTime", 1.0));
    newRelicMetricWhitelist.put("ControllerMaxCallTime",
        new MetricUnit("Call Time (ms)", "AppSvrCallTime", 1.0));
    newRelicMetricWhitelist.put("ControllerTotalCallTime",
        new MetricUnit("Call Time (ms)", "AppSvrCallTime", 1.0));

    gcpMetricWhitelist = new HashMap<String, MetricUnit>();
    gcpMetricWhitelist.put("memcache_total_operation",
        new MetricUnit("Memcache (# of Ops)", "Memcache", 1.0));
    gcpMetricWhitelist.put("memcache_sent",
        new MetricUnit("Memcache (MB)", "Memcache", 1.0 / 1024 / 1024));
    gcpMetricWhitelist.put("memcache_receive",
        new MetricUnit("Memcache (MB)", "Memcache", 1.0 / 1024 / 1024));
    gcpMetricWhitelist.put("network_sent",
        new MetricUnit("Network (MB)", "network", 1.0 / 1024 / 1024));
    gcpMetricWhitelist.put("network_received",
        new MetricUnit("Network (MB)", "network", 1.0 / 1024 / 1024));
    gcpMetricWhitelist.put("ce_disk_write", new MetricUnit("Disk (MB)", "disk", 1.0 / 1024 / 1024));
    gcpMetricWhitelist.put("ce_disk_read", new MetricUnit("Disk (MB)", "disk", 1.0 / 1024 / 1024));
    gcpMetricWhitelist.put("ce_network_send",
        new MetricUnit("Network (MB)", "network", 1.0 / 1024 / 1024));
    gcpMetricWhitelist.put("ce_network_receive",
        new MetricUnit("Network (MB)", "network", 1.0 / 1024 / 1024));
    gcpMetricWhitelist.put("ce_cpu_utilization", new MetricUnit("Total CPU (%)", "CPU", 100.0));

    customMetricWhitelist = new HashMap<String, MetricUnit>();
    customMetricWhitelist.put("CPU_utilization", new MetricUnit("(%)", "CPU", 1.0));
    customMetricWhitelist.put("CPU", new MetricUnit("(%)", "CPU", 1.0));
    customMetricWhitelist.put("NetworkOut", new MetricUnit("Network (MB)", "network", 1.0));
    customMetricWhitelist.put("NetworkIn", new MetricUnit("Network (MB)", "network", 1.0));
    customMetricWhitelist.put("DiskUsed", new MetricUnit("(%)", "disk", 1.0));
    customMetricWhitelist.put("DiskUsage", new MetricUnit("(%)", "disk", 1.0));
    customMetricWhitelist.put("MemoryUsage", new MetricUnit("(%)", "memory", 1.0));
    customMetricWhitelist.put("MemoryRequestUsage", new MetricUnit("(%)", "memory", 1.0));
    customMetricWhitelist.put("DiskFree", new MetricUnit("Disk (MB)", "disk", 1.0));
    customMetricWhitelist.put("MemUsed", new MetricUnit("Memory (MB)", "memory", 1.0));
    customMetricWhitelist.put("MemoryLimits", new MetricUnit("Memory (MB)", "memory", 1.0));
    customMetricWhitelist.put("MemoryRequests", new MetricUnit("Memory (MB)", "memory", 1.0));
    customMetricWhitelist.put("Memory", new MetricUnit("Memory (MB)", "memory", 1.0));
    customMetricWhitelist.put("MemFree", new MetricUnit("Memory (MB)", "memory", 1.0));
    customMetricWhitelist.put("DiskRead", new MetricUnit("Disk (MB)", "disk", 1.0));
    customMetricWhitelist.put("DiskWrite", new MetricUnit("Disk (MB)", "disk", 1.0));
    customMetricWhitelist.put("ReadRequestsCount",
        new MetricUnit("ReadRequests (# of Requests)", "Request", 1.0));
    customMetricWhitelist.put("MeanReadLatency",
        new MetricUnit("ReadLatency (ms)", "Latency", 1.0));
    customMetricWhitelist.put("MaxReadLatency", new MetricUnit("ReadLatency (ms)", "Latency", 1.0));
    customMetricWhitelist.put("MinReadLatency", new MetricUnit("ReadLatency (ms)", "Latency", 1.0));
    customMetricWhitelist.put("ReadReqRate(avg)",
        new MetricUnit("ReadReqRate (calls/sec)", "Request", 1.0));
    customMetricWhitelist.put("AvgReadReqRate",
        new MetricUnit("ReadReqRate (calls/sec)", "Request", 1.0));
    customMetricWhitelist.put("LoadAvg1", new MetricUnit("# of Active Tasks", "LoadAvg1", 1.0));
    customMetricWhitelist.put("LoadAvg5", new MetricUnit("# of Active Tasks", "LoadAvg5", 1.0));
    customMetricWhitelist.put("LoadAvg15", new MetricUnit("# of Active Tasks", "LoadAvg15", 1.0));

    customMetricWhitelist.put("cpuUsed", new MetricUnit("CPU (%)", "CPU", 1.0));
    customMetricWhitelist.put("cpuSystem", new MetricUnit("CPU (%)", "CPU", 1.0));
    customMetricWhitelist.put("cpuOverlap", new MetricUnit("CPU (%)", "CPU", 1.0));
    customMetricWhitelist.put("cpuRun", new MetricUnit("CPU (%)", "CPU", 1.0));
    customMetricWhitelist.put("cpuReady", new MetricUnit("CPU (%)", "CPU", 1.0));
    customMetricWhitelist.put("cpuWait", new MetricUnit("CPU (%)", "CPU", 1.0));
    customMetricWhitelist.put("DiskCommands", new MetricUnit("Disk (Commands/sec)", "disk", 1.0));
    customMetricWhitelist.put("DiskReadCommands",
        new MetricUnit("Disk (Commands/sec)", "disk", 1.0));
    customMetricWhitelist.put("DiskWriteCommands",
        new MetricUnit("Disk (Commands/sec)", "disk", 1.0));
    customMetricWhitelist.put("DiskReadRate", new MetricUnit("Disk (MB/s)", "disk", 1.0));
    customMetricWhitelist.put("DiskWriteRate", new MetricUnit("Disk (MB/s)", "disk", 1.0));
    customMetricWhitelist.put("NetworkIn/vSwitch0Total",
        new MetricUnit("Network (Mbps)", "network", 1.0));
    customMetricWhitelist.put("NetworkOut/vSwitch0Total",
        new MetricUnit("Network (Mbps)", "network", 1.0));
    customMetricWhitelist.put("NetworkIn/vSwitch1Total",
        new MetricUnit("Network (Mbps)", "network", 1.0));
    customMetricWhitelist.put("NetworkOut/vSwitch1Total",
        new MetricUnit("Network (Mbps)", "network", 1.0));

    customMetricWhitelist.put("NumberOfNodes", new MetricUnit("count", "Nodes", 1.0));
    customMetricWhitelist.put("NumberOfDataNodes", new MetricUnit("count", "Nodes", 1.0));
    customMetricWhitelist.put("NumberOfActivePrimaryShards",
        new MetricUnit("count", "Shards", 1.0));
    customMetricWhitelist.put("NumberOfActiveShards", new MetricUnit("count", "Shards", 1.0));
    customMetricWhitelist.put("NumberOfRelocatingShards", new MetricUnit("count", "Shards", 1.0));
    customMetricWhitelist.put("NumberOfInitializingShards", new MetricUnit("count", "Shards", 1.0));
    customMetricWhitelist.put("NumberOfUnAssignedShards", new MetricUnit("count", "Shards", 1.0));
    customMetricWhitelist.put("TotalGetRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("TotalIndexRequestsPerNode", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("TotalMerges", new MetricUnit("count", "Merges", 1.0));
    customMetricWhitelist.put("TotalQueryTime", new MetricUnit("ms", "Request", 1.0));
    customMetricWhitelist.put("TotalSearchRequestsPerNode",
        new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("GarbageCollectorTime", new MetricUnit("ms", "GC", 1.0));
    customMetricWhitelist.put("NumberOfDocumentsInNode", new MetricUnit("count", "Documents", 1.0));
    customMetricWhitelist.put("NumberOfDeletedDocuments",
        new MetricUnit("count", "Documents", 1.0));
    customMetricWhitelist.put("CurrentMerges", new MetricUnit("count", "Merges", 1.0));
    customMetricWhitelist.put("NumberOfSegments", new MetricUnit("count", "Segments", 1.0));
    customMetricWhitelist.put("CommittedHeap", new MetricUnit("MB", "memory", 1.0));
    customMetricWhitelist.put("UsedHeap", new MetricUnit("MB", "memory", 1.0));
    customMetricWhitelist.put("NumberOfOpenFileDescriptors",
        new MetricUnit("count", "FileDescriptor", 1.0));
    customMetricWhitelist.put("ProcessCpuPercent", new MetricUnit("%", "CPU", 1.0));
    customMetricWhitelist.put("RejectedBulkRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("RejectedFlushRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("RejectedGenericRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("RejectedGetRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("RejectedIndexRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("RejectedForceMergeRequests",
        new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("RejectedRefreshRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("RejectedSearchRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("RejectedSnapshotRequests", new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("TotalSearchRequestsPerCluster",
        new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("TotalIndexRequestsPerCluster",
        new MetricUnit("count", "Request", 1.0));
    customMetricWhitelist.put("FieldDataSize", new MetricUnit("MB", "DataSize", 1.0));
    customMetricWhitelist.put("NumberOfMerges", new MetricUnit("count", "Merges", 1.0));
    customMetricWhitelist.put("NumberOfDocumentsInCluster",
        new MetricUnit("count", "Documents", 1.0));

    customMetricWhitelist.put("BlockedProcess", new MetricUnit("count", "Process", 1.0));
    customMetricWhitelist.put("PagingProcess", new MetricUnit("count", "Process", 1.0));
    customMetricWhitelist.put("RunningProcess", new MetricUnit("count", "Process", 1.0));
    customMetricWhitelist.put("SleepingProcess", new MetricUnit("count", "Process", 1.0));
    customMetricWhitelist.put("StoppedProcess", new MetricUnit("count", "Process", 1.0));
    customMetricWhitelist.put("ZombieProcess", new MetricUnit("count", "Process", 1.0));

    customMetricWhitelist.put("DBSize", new MetricUnit("Records (count)", "DBSize", 1.0));
    customMetricWhitelist.put("Throughput", new MetricUnit("Requests/Minute", "Throughput", 1.0));
    customMetricWhitelist.put("Throughput", new MetricUnit("Requests/Minute", "Throughput", 1.0));
    customMetricWhitelist.put("AppSvrRespTime",
        new MetricUnit("Response Time (ms)", "AppSvrRespTime", 1.0));
    customMetricWhitelist.put("PayloadSize",
        new MetricUnit("Payload Size (Bytes)", "PayloadSize", 1.0));
    customMetricWhitelist.put("ReqReceiveTime",
        new MetricUnit("Avg Client Rcv Time (ms)", "ReqReceiveTime", 1.0));
    customMetricWhitelist.put("QueueTime",
        new MetricUnit("Avg Queue Delay (ms)", "QueueTime", 1.0));
    customMetricWhitelist.put("ConnectTime",
        new MetricUnit("Avg Connection Time (ms)", "ConnectTime", 1.0));
    customMetricWhitelist.put("TotalTime",
        new MetricUnit("Avg Overall Response Time (ms)", "TotalTime", 1.0));

    customMetricWhitelist.put("Throughput",
        new MetricUnit("I/O per Second (times)", "Throughput", 1.0));
    customMetricWhitelist.put("GC Heap:Bytes In Use_EPAgent",
        new MetricUnit("Bytes", "HeapUsed", 1.0));
    customMetricWhitelist.put("gc_cost", new MetricUnit("USD", "GC_Cost", 1.0));
    customMetricWhitelist.put("aws_cost", new MetricUnit("USD", "AWS_Cost", 1.0));
    allMetricWhitelist = new HashMap<String, MetricUnit>();
    allMetricWhitelist.putAll(awsMetricWhitelist);
    allMetricWhitelist.putAll(newRelicMetricWhitelist);
    allMetricWhitelist.putAll(gcpMetricWhitelist);
    allMetricWhitelist.putAll(customMetricWhitelist);
  }


  public static int CONNECTOR_CLIENT_TIMEOUT = 1000;

  // MapReduce default value
  public static final String MAPREDUCE_TYPE = "MapReduce";
  public static String DEFAULT_MAPREDUCE_HIS_PORT = "19888";
  public static final String HISTORY_TYPE = "History";
  public static String DEFAULT_MAPREDUCE_RES_PORT = "8088";
  public static final String MAPREDUCE_RESOURCE_TYPE = "Resource";
  public static String DEFAULT_MAPREDUCE_NODEMGR_PORT = "8042";
  public static final String MAPREDUCE_NODEMANAGER_TYPE = "NodeManager";

  // Spark default value
  public static final String SPARK_TYPE = "Spark";
  public static final String CONTEXT_TYPE = "Context";
  public static String DEFAULT_SPARK_HIS_PORT = "18080";
  public static String DEFAULT_SPARK_CURR_PORT = "4040";

  // Spark default value
  public static final String HBASE_TYPE = "HBase";
  // Sleep for 30 seconds when a write timeout occurs
  public static long writeTimeoutSleepDuration = 1000 * 30;
  // Sleep for 30 seconds when a write timeout occurs
  public static long readTimeoutSleepDuration = 1000 * 10;
  // Sleep for 30 seconds when a write timeout occurs
  public static long unavailableTimeoutSleepDuration = 1000 * 30;
  // The maximum log message data size before splitting occurs
  public static int maxDataLength = 10000;
  // Was 1500, increasing to get whole message for Pinterest
  public static int wordCountMaxStrLength = 1000;
  public static int maxLogEmailContentLength = 10000;

  public static boolean doLowerCaseKeywordComparison = true;
  public static final String IS_CRITICAL = "isCritical";
  public static final String TRACE_METHOD_NAME = "method_name";
  // Jenkins default
  public static final String JENKINS_TYPE = "Jenkins";
  public static String DEFAULT_JENKINS_PORT = "8080";
  // for keyword query page
  public final static String LOG_HIGHER_THAN_NORMAL_STR = "% higher than normal.";
  public final static String LOG_LOWER_THAN_NORMAL_STR = "% lower than normal.";
  public final static int defaultHotColdEventDuration = 1;
  public final static boolean runCacheTests = false;
  // order - true order matters, false order not matters,
  // it is the order of the words in the log entry
  public final static String tagHostname = "tag.Hostname";
  // InfluxDB Configurations
  public static final String JMX_BEANS = "beans";
  public static final String JMX_HBASE = "Hadoop:service=HBase";
  public static final String JMX_NAMENODE = "Hadoop:service=NameNode";
  public static final String JMX_DATANODE = "Hadoop:service=DataNode";
  public static final String JMX_YARN = "Hadoop:service=ResourceManager";
  public static final String MASTER = "Master";
  public static final String REGIONSERVER = "RegionServer";
  public static final String NAMENODE = "NameNode";
  public static final String DATANODE = "DataNode";
  public static final String YARN = "YARN";

  public static final int CASSANDRA_RETRY_COUNT = 3;
  // rabbitmq queue name
  public static final String TASK_TYPE_KEY = "TaskType";
  public static final String LONG_TERM = "LongTerm";
  public static final String TASK_TYPE_KEY_APPFORECAST = "AppForecast";
  public static final String TASK_TYPE_KEY_APPSTATS = "AppStats";
  public static final String TASK_TYPE_KEY_OUTAGESUPPORT = "OutageSupport";
  public static final String APP_PREDICTION_RELATED_QUEUE = "AppPredictionRelated-queue";
  public static final String RETENTION_QUEUE = "Retention-queue";
  public static final String LONG_TERM_APP_FORECAST_QUEUE = "longTerm-appforecast-queue";
  public static final String DETECT_PULL_QUEUE = "detect-pull-queue";
  public static final String EVENT_SUPPORT_QUEUE = "EventsSupport-queue";
  public static final String EVENT_SUPPORT_REPLAY_QUEUE = "EventsSupport-Replay-queue";
  public static final String CUSTOM_PROJECT_DATA_QUEUE = "CustomProjectData-queue-durable";
  public static final String BINARY_FILE_QUEUE = "BinaryFile-queue";
  public static final String TOPOLOGY_LOG_DATA_QUEUE = "TopologyData-queue";
  public static final String PRIMARY_CRON_COLLECT_QUEUE = "primary-cron-collect-queue";
  public static final String RERUN_CRON_COLLECT_QUEUE = "rerun-cron-collect-queue";
  public static final String DIFF = "diff";
  public static final float DIFF_TIME_OUT = 0;
  public static final String DECOMPRESS_INSERT = "I";
  public static final String DECOMPRESS_DELETE = "D";
  public static final String DECOMPRESS_EQUAL = "E";

  // TODO: talk this with Mo
  public static final int DEFAULT_SUBINSTANCELIST_SIZE = 10;

  public static final String JMX_METRIC_OBJ_HOSTNAME = "hostname";

  public static final boolean INCLUDE_YARN_HBASE = false;
  public static final boolean INCLUDE_DATANODE_HBASE = false;
  public static final boolean INCLUDE_REGIONSERVER_HBASE = true;
  public static final boolean INCLUDE_NAMENODE_HBASE = false;


  // Metric Data Table
  public static final int METRIC_CHUNKING_SIZE = 300;
  // Project Metric Setting Table
  public static final String METRIC_SETTING_NONE = "None";
  public static final String METRIC_SETTING_GLOBAL_PREFIX = "Global_";
  public static final String COMPUTE_DIFFERENCE = "computeDifference";
  public static final int INSTANCE_PRIORITY_HIGH = 5;
  public static final int INSTANCE_PRIORITY_BASE = 3;
  public static final boolean IGNORE_FLAG = false;

  public static final int LOG_NUM_OF_TOP_CLUSTER_KEYWORDS = 10;
  public static final float TOP_25_PERCENTAGE = 0.25f;
  public static final float TOP_50_PERCENTAGE = 0.5f;
  public static final float TOP_75_PERCENTAGE = 0.75f;
  public static final float TOP_100_PERCENTAGE = 1.0f;
  public static final Float[] LOG_KEYWORDS_PERCENTAGE_ARRAY = {TOP_25_PERCENTAGE, TOP_50_PERCENTAGE,
      TOP_75_PERCENTAGE, TOP_100_PERCENTAGE};
  public static final int RARE_NID_BASE = 10000;
  public static final String WORD_BOUNDARY = "\\b";
  public static final String JSON_PATH_CONNECTOR = "->";
  // Since the consine similarity need a threshold to check whetehr return 0 or the similarity rate
  // For normal use just set it as 0
  public static final float NORMAL_SIMILARITY_THRESHOLD = ZERO_FLOAT;
  // Before pattern id for log is not consisitency, we need to store the pattern name based on the timestamp
  // Now with the pattern name consistency feature, we can set the pattern name based on the
  // instance, so just set a constant timestamp as part of the primary key
  public static final String LOG_DATA_KEY = "data";
  public static final String LOG_EVENT_TYPE = "logEventType";
  public static final String LOG_PATTERN_NAME_KEY = "patternName";
  public static final String LOG_NEW_PATTERN_FLAG_KEY = "newPatternFlag";
  public static final String LOG_INCIDENT_FLAG = "incidentFlag";
  public static final String LOG_HIGHLIGHT_FLAG_KEY = "highlightFlag";
  public static final String LOG_SAMPLE_MSG = "sampleMsg";
  public static final String LOG_ANOMALY_WORDS = "anomalyWords";
  public static final String LOG_INDEX_KEY = "index";
  public static final String LOG_TOPK_KEY = "topK";
  public static final String LOG_CATEGORY_KEY = "category";
  public static final String LOG_ANOMALY_SCORE = "anomalyScore";
  public static final String LOG_NEURON_ID = "neuronId";
  public static final String LOG_IS_IGNORED_KEY = "isIgnored";
  // Cluster size threshold to decided whether display on the log analysis page or not
  public static final int CLUSTER_DISPLAY_THRESHOLD = 100;
  // Cluster's log entry count threshold to decided whether count in the log calendar info of the
  // number of cluster
  public static final int rareSampleTrainingIterations = 10;
  public static final int trainingIterationIntervalThreshold = 600;
  public static final long SEND_SPLUNK_DATA_TIME_WINDOW = FIVE_MINS_IN_MILLIS;
  // triage report setting
  public static int NUM_OF_TRIAGE_REPORT_LIMIT = 10;

  // port for worker health check
  public static final int WORKER_HEALTH_CHECK_PORT = 8080;  // TODO: check the string
  public static final int WORKER_HEALTH_CHECK_PORT_SSL = 8443;

  // TODO: check the string
  // Elastic Search details
  public static final String ELASTIC_SEARCH_TYPE = "ElasticSearch";
  public static String ELASTIC_SEARCH_CLUSTER_HEALTH_URL = "_cluster/health";
  public static String ELASTIC_SEARCH_ALL_STATS_URL = "_all/_stats#";
  public static String ELASTIC_SEARCH_NODES_LOCAL_URL = "_nodes/_local";
  public static final String PROJECT_TYPE_CUSTOM = "CUSTOM";
  public static final String PROJECT_TYPE_AWS = "AWS";
  public static final String PROJECT_TYPE_GCE = "GCE";
  public static final String PROJECT_TYPE_SERVICE_NOW = "ServiceNow";
  public static final String PROJECT_TYPE_ZENDESK = "Zendesk";
  public static final String PROJECT_TYPE_BIGQUERY = "Bigquery";
  public static final String PROJECT_TYPE_SUMOLOGIC = "SumoLogic";
  public static final String PROJECT_TYPE_SHADOW = "Shadow";
  public static final String PROJECT_TYPE_EC2 = "EC2";
  public static final String PROJECT_TYPE_ECS = "ECS";
  public static final String CAUSAL_LOG_RARE_EVENT_TYPE = "LogRareEvent";
  public static final String CAUSAL_LOG_HOT_EVENT_TYPE = "LogHotEvent";
  public static final String CAUSAL_LOG_COLD_EVENT_TYPE = "LogColdEvent";
  public static final String CAUSAL_LOG_CRITICAL_EVENT_TYPE = "LogCriticalEvent";
  public static final String CAUSAL_LOG_WHITELIST_EVENT_TYPE = "LogWhiteListEvent";
  public static final String CAUSAL_LOG_NEW_PATTERN_EVENT_TYPE = "LogNewPatternEvent";
  public static final String CAUSAL_LOG_NORMAL_EVENT_TYPE = "LogNormalEvent";
  public static final String CAUSAL_LOG_ALERT_EVENT_TYPE = "LogAlertEvent";
  public static final String CAUSAL_LOG_DATA_QUALITY_EVENT_TYPE = "LogDataQualityEvent";
  public static final String CLAZZ = "clazz";
  public static final String CLAUSE_LIST = "clauseList";
  public static final String BUCKET = "bucket";
  public static final String FILE_NAME_LIST = "fileNameList";
  public static final long OUTDATED_PATTERN_TIMESTAMP = ONE_DAY_IN_MILLIS * 7;
  public static final int METRIC_COLLECTION_TIMEOUT_VALUE = 10 * 1000;
  // Alert similarity thresholds
  public static final int LOG_RAW_DATA_MAX_LENGTH = 5000;
  public static final String LOG_MODEL_UPDATING = "update";
  public static final String LOG_MODEL_CREATING = "create";
  public static final String CONFIG_LOG_CRON_TRAINING_SPAN_MILLIS = "LOG_CRON_TRAINING_SPAN_MILLIS";
  public static final String CONFIG_TEMP_PWD_EXPIRE_TIME = "TEMP_PWD_EXPIRE_TIME";
  public static final String PERCENTAGE_STR = "percentageStr";
  public static final String PATTERN_NAME_PREFIX = "Pattern ";
  public static final String AND_STRING = "\"AND\"";
  public static final String OR_STRING = "\"OR\"";
  public static final char QUOTATION_MARK = '\"';
  public static final String ENV_WITH_COLON = "Env:";
  public static final String MODEL_KEY = "modelKey";
  public static final String PICKABLE_FLAG = "pickableFlag";
  public static final String SAMPLE_COUNT = "sampleCount";
  public static final String METRIC_NAME_LIST = "metricNameList";
  public static final String MIN_VALUES = "minValues";
  public static final String MAX_VALUES = "maxValues";
  public static final String SEASON_TYPE = "seasonType";
  public static final String MAP_DATA = "mapData";
  public static final String METRIC_NAME_SPLITTER = "\\[";
  public static final int DEFAULT_WORD_COUNT_LIMIT = 10;
  public static final int ASCENDING_ORDER = 1;
  public static final int DESCENDING_ORDER = 0;
  public static final String USER_DEINFED_CATEGORY = "IF_userDefinedCategory";

  public static final String JSON_CONTENT_TYPE = "application/json";
  public static final String BASIC_AUTH = "Basic ";
  public static final String PAGER_DUTY_AUTH = "Token token=";
  public static final String SUCCESS_RESP = "success";
  public static final String FAIL_RESP = "fail";
  public static final String HTTP_CONTENT_TYPE = "Content-type";
  public static final String HTTP_AUTHORIZATION = "Authorization";
  public static final String HTTP_ACCEPT = "Accept";
  public static final int UNSET_PATTERN_ID = -1;
  public static final int DEFAULT_PROJECT_RETENTION_TIME = 90;
  public static final int PATTERN_CLUSTER_KEYWORD_LIMIT = 3;
  public static final String INSTANCE_ID = "instanceId";
  public static final String SPACE = " ";
  public static final double NUM_OF_MINUTES_IN_ONE_DAY = 1440.0;
  public static final String EVENT = "event";
  public static final String COMPONENT_NAME = "componentName";
  public static final String CONTENT = "content";
  public static final String USER_STATUS_ACTIVE = "active";
  public static final String USER_STATUS_INACTIVE = "inactive";
  public static final String ZONE = "Zone";
  public static final String APP_NAME = "appName";
  public static final int DEFAULT_MAXIMUM_THREAD_COUNT = 1;
  //model's day coverage should cover 95% of the model span
  public static final double LOG_MODEL_UPDATE_THRESHOLD = 0.95;
  public static final String NA = "N/A";
  public static final int IGNORE = 1;
  public static final int NOT_IGNORE = 0;
  public static final int RCA_FLAG = 1;
  public static final int NOT_RCA_FLAG = 2;
  public static final String EQUAL_SIGN = "=";
  public static final String TRUE_STR = "true";
  public static final String UI_HOST_CONFIG = "IF_UI_HOST";
  public static final String GMT = " GMT";
  public static final String HYPHEN = "-";
  public static final String EMPTY_SET = "[]";
  // insights cron
  public static final long DEFAULT_INCIDENT_DURATION_INTERVAL = 10 * ONE_MINUTE;

  // rca related
  public static final String METADATA = "metadata";
  public static final String RELEVANCE = "relevance";
  public static final String ROOT_CAUSE_KEY = "rootCauseKey";

  // cost related
  public static final Float INCIDENT_COST_DEFAULT_VALUE = 5000.0f;
  public static final String INSTANCE_DOWN = "instanceDown";
  public static final String INSTANCE_DOWN_METRIC_PREFIX = "INSTANCE_DOWN_";
  public static final int INSTANCE_DOWN_PATTERN_ID = 99999;
  // rule related
  public static final String GENERIC_RULE_INSTANCE = "genericRule";
  public static final String LOWER = "lower";
  public static final float METRIC_VALUE_DEFAULT = 1.0f;
  public static final float METRIC_PERCENTAGE_DEFAULT = -100.0f;
  public static final String GLOBAL_LEVEL_RULE_DEFAULT = "-1880685610";
  public static final String RULETYPE = "ruleType";
  public static final String PREDICTIONRULEINFO = "predictionRuleInfo";
  public static final String RULES = "rules";
  public static final int SINGLESRCRULEGAP = 12;
}
