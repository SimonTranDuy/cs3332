package cs3332.project.cs3332.service;

import cs3332.project.cs3332.model.SystemSetting;
import cs3332.project.cs3332.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingService {

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    public boolean isLoginAllowed() {
        return systemSettingRepository.findByKey("isLoginAllowed")
                .map(setting -> Boolean.parseBoolean(setting.getValue()))
                .orElse(false); // Mặc định là false nếu không có cài đặt
    }

    public void setLoginAllowed(boolean isAllowed) {
        SystemSetting setting = systemSettingRepository.findByKey("isLoginAllowed")
                .orElse(new SystemSetting());
        setting.setKey("isLoginAllowed");
        setting.setValue(Boolean.toString(isAllowed));
        systemSettingRepository.save(setting);
    }
}
