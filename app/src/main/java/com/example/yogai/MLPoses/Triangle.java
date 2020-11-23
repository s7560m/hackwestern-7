package com.example.yogai.MLPoses;

import com.example.yogai.Angles;
import com.google.mlkit.vision.pose.Pose;

public class Triangle {
    Angles angle = new Angles();
    public boolean isValidPose(Pose pose) {
        // return true if all poses are valid
        if (angle.leftShoulderAngle(pose) > 70 && angle.leftShoulderAngle(pose) < 90) {
            if (angle.rightShoulderAngle(pose) > 80 && angle.leftShoulderAngle(pose) < 90) {
                if (angle.rightHipAngle(pose) > 30 && angle.rightHipAngle(pose) < 50) {
                    if (angle.leftHipAngle(pose) > 80 && angle.leftHipAngle(pose) < 90) {
                        return true;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
