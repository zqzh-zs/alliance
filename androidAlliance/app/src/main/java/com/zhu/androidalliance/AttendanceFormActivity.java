package com.zhu.androidalliance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zhu.androidalliance.callback.JsonCallback;
import com.zhu.androidalliance.common.Commons;
import com.zhu.androidalliance.enums.BehaviorType;
import com.zhu.androidalliance.utils.MeetingDataTracker;
import com.zhu.androidalliance.utils.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


public class AttendanceFormActivity extends AppCompatActivity {

    private TextInputEditText name, org, phone, email, train, time;
    private MaterialButton submit;

    // 邮箱验证正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    // 手机号验证正则表达式（简化版）
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_form);

        // 初始化控件
        initViews();

        // 设置提交按钮点击事件
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    // 表单验证通过，处理提交逻辑
                    submitForm();
                }
            }
        });
    }

    private void initViews() {
        name = findViewById(R.id.editName);
        org = findViewById(R.id.editOrg);
        phone = findViewById(R.id.editPhone);
        email = findViewById(R.id.editEmail);
        train = findViewById(R.id.editTrain);
        time = findViewById(R.id.editTime);
        submit = findViewById(R.id.buttonSubmit);
    }

    private boolean validateForm() {
        // 获取输入内容
        String nameText = name.getText().toString().trim();
        String orgText = org.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String trainText = train.getText().toString().trim();
        String timeText = time.getText().toString().trim();

        // 验证姓名
        if (TextUtils.isEmpty(nameText)) {
            name.setError("请输入姓名");
            name.requestFocus();
            return false;
        }

        // 验证单位
        if (TextUtils.isEmpty(orgText)) {
            org.setError("请输入单位");
            org.requestFocus();
            return false;
        }

        // 验证手机号
        if (TextUtils.isEmpty(phoneText)) {
            phone.setError("请输入手机号");
            phone.requestFocus();
            return false;
        } else if (!PHONE_PATTERN.matcher(phoneText).matches()) {
            phone.setError("请输入有效的手机号");
            phone.requestFocus();
            return false;
        }

        // 验证邮箱
        if (!TextUtils.isEmpty(emailText) && !EMAIL_PATTERN.matcher(emailText).matches()) {
            email.setError("请输入有效的电子邮箱");
            email.requestFocus();
            return false;
        }

        // 验证到达方式/车次
        if (TextUtils.isEmpty(trainText)) {
            train.setError("请输入到达方式/车次");
            train.requestFocus();
            return false;
        }

        // 验证到达时间
        if (TextUtils.isEmpty(timeText)) {
            time.setError("请输入到达时间");
            time.requestFocus();
            return false;
        }

        return true;
    }

    private void submitForm() {
        // 获取输入内容
        String nameText = name.getText().toString().trim();
        String orgText = org.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String trainText = train.getText().toString().trim();
        String timeText = time.getText().toString().trim();
        // 创建 JSON 对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", nameText);
            jsonObject.put("organization", orgText);
            jsonObject.put("phone", phoneText);
            jsonObject.put("email", emailText);
            jsonObject.put("trip", trainText);
            jsonObject.put("time", timeText);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "创建JSON失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String jsonData = jsonObject.toString();

        // 后端API地址
        String apiUrl = Commons.BASE_HOST+ "/api/submitAttendance";

        // 使用新的 JSON 发送方法
        OkHttpUtil.doPostJson(apiUrl, new JsonCallback() {
            @Override
            public void onSuccess(String json) {
                runOnUiThread(() -> {
                    Toast.makeText(AttendanceFormActivity.this, "提交成功", Toast.LENGTH_SHORT).show();

                    // 获取会议ID并记录提交行为
                    Integer meetingId = getIntent().getIntExtra("meetingId", -1);
                    if (meetingId != -1) {
                        MeetingDataTracker.track(meetingId, BehaviorType.SUBMIT);
                    }
                    new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 1500);
                });
            }

            @Override
            public void onError(String errorMsg) {
                runOnUiThread(() ->
                        Toast.makeText(AttendanceFormActivity.this, "提交失败: " + errorMsg, Toast.LENGTH_LONG).show()
                );
            }
        }, jsonData);

    }
}