package com.example.springboot.service.host;

import com.example.springboot.controller.dto.host.HostSaveRequestDto;
import com.example.springboot.domain.host.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HostServiceImpl implements HostService {

    private final HostRepository hostRepository;
    private final HostMainImgRepository hostMainImgRepository;
    private final HostImgRepository hostImgRepository;
    String filepath = "C:\\Users/rlawl/IdeaProjects/WWOOF-side-project/src/main/resources/static/img/Host/";

    @Override
    public String save(HostSaveRequestDto requestDto, MultipartFile file) {
        // Host 데이터 등록
        // 처음 등록이기 때문에 (update 시 role 이 admin 인경우에 Y로 변경)
        requestDto.setApprvYn("N");
        Host host = hostRepository.save(requestDto.toEntity());
        // 1. 호스트 정보에 대해서 등록한 후

        // 2. 그 번호를 가지고 이름을 임의로 지정한 후 저장
        String hostNum = String.valueOf(host.getHnum());
        String fileName = "["+hostNum+"]MainImg";
        File file1 = new File(filepath+fileName);
        try {
            file.transferTo(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final HostMainImg hostMainImg = HostMainImg.builder()
                .hnum(host.getHnum())
                .filename(fileName)
                .build();
        hostMainImgRepository.save(hostMainImg);

        // 3. 이미지 등록을 위해서 hostnum을 넘김
        return String.valueOf(hostNum);
    }

    @Override
    public void saveImgs(MultipartFile[] files, String hostNum) {
        Long hnum = Long.parseLong(hostNum);

        // Host 이미지 등록 (각 파일마다 등록)
        for (int i = 0; i < files.length; i++) {
            String fileName = "["+hostNum+"] images "+(i+1);
            File file1 = new File(filepath+fileName);
            try {
                files[i].transferTo(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final HostImg img = HostImg.builder()
                    .hostImg_turn(Long.valueOf(i+1))
                    .hnum(hnum)
                    .filename(fileName)
                    .build();
            hostImgRepository.save(img);
        }
    }
}
