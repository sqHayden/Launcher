package com.idx.jakku.calendar;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ryan on 18-6-5.
 * Email: Ryan_chan01212@yeah.net
 */

public class TimeBean {

    /**
     * ret : 200
     * data : {"action":"execute","content":{"display":"建军节是8月1日","display_guide":[],"error_code":0,"reply":{"default":[{"city":"","day":1,"day_val":"2018-08-01","form_val":"2018-08-01:00:00:00","hour":-1,"min":-1,"month":8,"sec":-1,"time_val":"00:00:00","year":2018}]},"semantic":{"Festival":["建军节"]},"summary":{},"total_count":1,"tts":"建军节是8月1日","type":"default"},"domain":"time","intention":"searching","query":"建军节是哪一天","queryid":"626c673b-932d-43fa-b2b6-9cadd8370628141199752","terms":"建军 节 是 哪 一天"}
     * msg :
     */

    private int ret;
    private DataBean data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * action : execute
         * content : {"display":"建军节是8月1日","display_guide":[],"error_code":0,"reply":{"default":[{"city":"","day":1,"day_val":"2018-08-01","form_val":"2018-08-01:00:00:00","hour":-1,"min":-1,"month":8,"sec":-1,"time_val":"00:00:00","year":2018}]},"semantic":{"Festival":["建军节"]},"summary":{},"total_count":1,"tts":"建军节是8月1日","type":"default"}
         * domain : time
         * intention : searching
         * query : 建军节是哪一天
         * queryid : 626c673b-932d-43fa-b2b6-9cadd8370628141199752
         * terms : 建军 节 是 哪 一天
         */

        private String action;
        private ContentBean content;
        private String domain;
        private String intention;
        private String query;
        private String queryid;
        private String terms;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getIntention() {
            return intention;
        }

        public void setIntention(String intention) {
            this.intention = intention;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getQueryid() {
            return queryid;
        }

        public void setQueryid(String queryid) {
            this.queryid = queryid;
        }

        public String getTerms() {
            return terms;
        }

        public void setTerms(String terms) {
            this.terms = terms;
        }

        public static class ContentBean {
            /**
             * display : 建军节是8月1日
             * display_guide : []
             * error_code : 0
             * reply : {"default":[{"city":"","day":1,"day_val":"2018-08-01","form_val":"2018-08-01:00:00:00","hour":-1,"min":-1,"month":8,"sec":-1,"time_val":"00:00:00","year":2018}]}
             * semantic : {"Festival":["建军节"]}
             * summary : {}
             * total_count : 1
             * tts : 建军节是8月1日
             * type : default
             */

            private String display;
            private int error_code;
            private ReplyBean reply;
            private SemanticBean semantic;
            private SummaryBean summary;
            private int total_count;
            private String tts;
            private String type;
            private List<?> display_guide;

            public String getDisplay() {
                return display;
            }

            public void setDisplay(String display) {
                this.display = display;
            }

            public int getError_code() {
                return error_code;
            }

            public void setError_code(int error_code) {
                this.error_code = error_code;
            }

            public ReplyBean getReply() {
                return reply;
            }

            public void setReply(ReplyBean reply) {
                this.reply = reply;
            }

            public SemanticBean getSemantic() {
                return semantic;
            }

            public void setSemantic(SemanticBean semantic) {
                this.semantic = semantic;
            }

            public SummaryBean getSummary() {
                return summary;
            }

            public void setSummary(SummaryBean summary) {
                this.summary = summary;
            }

            public int getTotal_count() {
                return total_count;
            }

            public void setTotal_count(int total_count) {
                this.total_count = total_count;
            }

            public String getTts() {
                return tts;
            }

            public void setTts(String tts) {
                this.tts = tts;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<?> getDisplay_guide() {
                return display_guide;
            }

            public void setDisplay_guide(List<?> display_guide) {
                this.display_guide = display_guide;
            }


            public static class ReplyBean {
                @SerializedName("default")
                private List<DefaultBean> defaultX;

                private List<TimeBean_1> time;
                private List<DateBean> date;

                public List<DateBean> getDate() {
                    return date;
                }

                public void setDate(List<DateBean> date) {
                    this.date = date;
                }

                public List<TimeBean_1> getTime() {
                    return time;
                }

                public void setTime(List<TimeBean_1> time) {
                    this.time = time;
                }

                public List<DefaultBean> getDefaultX() {
                    return defaultX;
                }

                public void setDefaultX(List<DefaultBean> defaultX) {
                    this.defaultX = defaultX;
                }


                public static class DateBean {
                    /**
                     * date_str : 2018-06-06
                     * day : 6
                     * month : 6
                     * year : 2018
                     */

                    private String date_str;
                    private int day;
                    private int month;
                    private int year;

                    public String getDate_str() {
                        return date_str;
                    }

                    public void setDate_str(String date_str) {
                        this.date_str = date_str;
                    }

                    public int getDay() {
                        return day;
                    }

                    public void setDay(int day) {
                        this.day = day;
                    }

                    public int getMonth() {
                        return month;
                    }

                    public void setMonth(int month) {
                        this.month = month;
                    }

                    public int getYear() {
                        return year;
                    }

                    public void setYear(int year) {
                        this.year = year;
                    }
                }


                public static class TimeBean_1 {
                    /**
                     * city : 深圳
                     * date : 2018-06-05
                     * fulltime : 2018-06-05:11:28:32
                     * hour : 11
                     * min : 28
                     * sec : 32
                     * time_str : 11:28:32
                     */

                    private String city;
                    private String date;
                    private String fulltime;
                    private int hour;
                    private int min;
                    private int sec;
                    private String time_str;

                    public String getCity() {
                        return city;
                    }

                    public void setCity(String city) {
                        this.city = city;
                    }

                    public String getDate() {
                        return date;
                    }

                    public void setDate(String date) {
                        this.date = date;
                    }

                    public String getFulltime() {
                        return fulltime;
                    }

                    public void setFulltime(String fulltime) {
                        this.fulltime = fulltime;
                    }

                    public int getHour() {
                        return hour;
                    }

                    public void setHour(int hour) {
                        this.hour = hour;
                    }

                    public int getMin() {
                        return min;
                    }

                    public void setMin(int min) {
                        this.min = min;
                    }

                    public int getSec() {
                        return sec;
                    }

                    public void setSec(int sec) {
                        this.sec = sec;
                    }

                    public String getTime_str() {
                        return time_str;
                    }

                    public void setTime_str(String time_str) {
                        this.time_str = time_str;
                    }
                }


                public static class DefaultBean {
                    /**
                     * city :
                     * day : 1
                     * day_val : 2018-08-01
                     * form_val : 2018-08-01:00:00:00
                     * hour : -1
                     * min : -1
                     * month : 8
                     * sec : -1
                     * time_val : 00:00:00
                     * year : 2018
                     */
                    private String str;
                    private String city;
                    private int day;
                    private String day_val;
                    private String form_val;
                    private int hour;
                    private int min;
                    private int month;
                    private int sec;
                    private String time_val;
                    private int year;

                    public String getStr() {
                        return str;
                    }

                    public void setStr(String str) {
                        this.str = str;
                    }

                    public String getCity() {
                        return city;
                    }

                    public void setCity(String city) {
                        this.city = city;
                    }

                    public int getDay() {
                        return day;
                    }

                    public void setDay(int day) {
                        this.day = day;
                    }

                    public String getDay_val() {
                        return day_val;
                    }

                    public void setDay_val(String day_val) {
                        this.day_val = day_val;
                    }

                    public String getForm_val() {
                        return form_val;
                    }

                    public void setForm_val(String form_val) {
                        this.form_val = form_val;
                    }

                    public int getHour() {
                        return hour;
                    }

                    public void setHour(int hour) {
                        this.hour = hour;
                    }

                    public int getMin() {
                        return min;
                    }

                    public void setMin(int min) {
                        this.min = min;
                    }

                    public int getMonth() {
                        return month;
                    }

                    public void setMonth(int month) {
                        this.month = month;
                    }

                    public int getSec() {
                        return sec;
                    }

                    public void setSec(int sec) {
                        this.sec = sec;
                    }

                    public String getTime_val() {
                        return time_val;
                    }

                    public void setTime_val(String time_val) {
                        this.time_val = time_val;
                    }

                    public int getYear() {
                        return year;
                    }

                    public void setYear(int year) {
                        this.year = year;
                    }
                }
            }

            public static class SemanticBean {
                private List<String> Festival ;
                private List<String> TIME ;
                private List<String> TimeCountType ;

                public List<String> getFestival() {
                    return Festival;
                }

                public void setFestival(List<String> festival) {
                    Festival = festival;
                }

                public List<String> getTIME() {
                    return TIME;
                }

                public void setTIME(List<String> TIME) {
                    this.TIME = TIME;
                }

                public List<String> getTimeCountType() {
                    return TimeCountType;
                }

                public void setTimeCountType(List<String> timeCountType) {
                    TimeCountType = timeCountType;
                }
            }

            public static class SummaryBean {
            }
        }
    }
}
