package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtEntity.NqtSetting;
import k23cnt1.nqt.project3.nqtRepository.NqtSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class NqtSettingService {
    @Autowired
    private NqtSettingRepository nqtSettingRepository;

    public String getNqtValue(String nqtName) {
        return getNqtValue(nqtName, "");
    }

    public String getNqtValue(String nqtName, String defaultValue) {
        Optional<NqtSetting> setting = nqtSettingRepository.findByNqtName(nqtName);
        if (setting.isPresent()) {
            return setting.get().getNqtValue();
        } else {
            NqtSetting newSetting = new NqtSetting();
            newSetting.setNqtName(nqtName);
            newSetting.setNqtValue(defaultValue);
            nqtSettingRepository.save(newSetting);
            return defaultValue;
        }
    }

    public void saveNqtValue(String nqtName, String nqtValue) {
        Optional<NqtSetting> settingOptional = nqtSettingRepository.findByNqtName(nqtName);
        NqtSetting setting;
        if (settingOptional.isPresent()) {
            setting = settingOptional.get();
        } else {
            setting = new NqtSetting();
            setting.setNqtName(nqtName);
        }
        setting.setNqtValue(nqtValue);
        nqtSettingRepository.save(setting);
    }
}
