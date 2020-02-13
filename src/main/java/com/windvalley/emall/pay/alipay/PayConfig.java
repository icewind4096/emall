package com.windvalley.emall.pay.alipay;

public class PayConfig {
    /**
     * 支付宝公钥
     * 用户公钥 用于产生支付宝公钥,以后好像没用
     */
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyTUmD2A9/xlRg67nrVCTvuMEV4MJvH5ywd4lI4lL03MuYgHM0MtTeSaMm/ErHzLLZb3XzvfvTTl/K3KS+9QiQmPR59o3nL4qe1HQQyfJQcx4DtDwIW6kEXmZ+d9zrZinlf1jtRPngqulAM1nSf+970kgmZ2P3zWsxFnM1+G2m31XPblnHcGicRk7ds7Qjmg3KsaymjmyD0LotvXJSvPCEiUncAw5PR615dJoUAJJr4JYQImo0+mUKXRBvttuiqyOwcMO+RFXD3Echp0JC+UhZ3ADWfmdUqn9qgZWpKJvfcjKUiBSnwG1BD+kwoavIBJkJMPOAS/mIEmhvuQHTsNLsQIDAQAB";

    /**
     * 商户私钥
     */
    public static String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7WcjE6EZUkQCO/kcxCOxbJLLEnZRVRFmxdAhDP32c2fFgK3axqMGz678fQ/5nwj2UJ8bP4MpMJlzb/WaWLol+lbidoQgp2u29r69YcY195vUH7/Et+NxCdQmh4+nwGgm7ci9nft6RA/TSZobymktPIbRZSMU4GX2QtRHWphmAPTW+w3cqdF0sRdxwtZx1c3zAcX3u2Qn5TcA6ML/GLN9/u9FWDIP8yc6hI2tK6s3P9+f3b3f/o9CR3x+gj8Q08Nh3NTeY/9T33w0KvWzJ4PKgMHtzOidVXseuMQEv6azF+SRRXJWICAgmCc8d4ZU6ZkVzGjD/Hu477fXq9jw6Gud9AgMBAAECggEAeXF/1TQDsvmzdP2bREvWelZVk2HaL1N5GLBwUrcrQ6t67a3+6LbNqUVdxHScysF8jTcjhsjeGXKcqvWdtChCdE21OGiUFB2YEmd5sEkbQMufdh+xJO22SVX4gnpswHkAagNgeyQgPbxkzKYtP8GIo6jjcSTcwrqzTEQzZgKRcTAfXLf+oU2q+me2mF9kjPWk8iWJo5bjknSB73f4MRi5MEkm+2+Z219OlTocagL6jHdauioFtoKJEKJkyMxpik9TzzLrYi6QJq9JiGY88Cmp2iPxUPXlXifMtsopd1AfSvI2EFZZYK2ZhuX71GDakF5odBbJIC3quBKV8aRGoGKueQKBgQDuc7XzkS2VKOPcesOCulblGn39Okf1AHaKCVWOz18xnzKhASXJlgUTEXPwfv8cufmuFVA0P3lpm37M9I5gvKHDkH5wHT9sPDKbZZEgqolvabYxMK8DRyONs3ZyfpdHgK4l6Md0wAHRU2v/SX2MLpnDmmtJ4RSL69JeAVhITCGkBwKBgQDJI1tZ0XASAEHTm7YTPqXXzZyn+f79m1n/2QhdZNu339KghY1m2ZgwKajjqWUW/YQFo9srsPp5KoqRu9KcsHRoDtfiDUH3xdAXyUIzO3fM2kq8SsSRQoryQOEM6wrKP9MCJ0gLTFbORGdkVY/NmPKr5LdCep52q0leD5Fkk8FfWwKBgQCmyhceTJE2wUhJAzHYMiDv1c0EoIyOiglgWlEXOGQcoH9YcSYOUDoycUXIlfw6CrfjlZLpSPDS9uoF0JX6glcgJOTb5Qlk2uKHIc8Wq1LAtI+07pmsUElFJ5+VTIjigdbOO3mwZ1GeKpzjD3Oa50m0sUGUhrTJfmVCTeyDfYUWZwKBgCSWItq+pwUPOOoV69OqVJ5hjzpa1hApfwBz6PqcCv9yXizGvkbUE8PHACqROIsrCCXCfW6AHb+Ghngl0xSfYD1BXfGHedVpDQYWnM0W5x7DIk8HxqOpl/6i4IzqACdz3p0Iqikr9KpGdQwiKCs8w/+SdedIyKRLWegbKtZBri37AoGAMS1A2ihW5GVpFFi2bhg3nN8xRu5YoIKljqeits9cgtwxeegvZXUrkoeqPbYCKz3YYzilbrLxXsMiuSxkaL8b97BgK/ALSsnhMxTZm4sdUw6oV3slfzzGVvKdrigZhG2c0I8UY+o33L3XmgjBd5GjIdOlGy+5vCq9j7OkOj6kLiI=";

    /**
     * 支付宝APPID
     */
    public static String APPID = "2016091800540901";

    /**
     * 请求网关
     */
    public static String SERVERURL = "https://openapi.alipaydev.com/gateway.do";

    /**
     * 回调地址
     */
    public static String ALIPAY_NOTIFY_URL = "http://xfh2gn.natappfree.cc/emall/order/alipaycallback.do";

    /**
     * 字符集
     */
    public static String CHARSET = "utf-8";

    /**
     * 签名类型
     */
    public static String SIGN_TYPE = "RSA2";

    /**
     * 格式
     */
    public static String FORMAT = "json";

    /**
     * 商户代码
     */
    public static String SELLID = "2088102176202274";
}
