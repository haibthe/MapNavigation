package com.hb.map.navigation.v1.navigation;

import com.hb.map.navigation.v1.navigation.camera.Camera;
import com.hb.map.navigation.v1.navigation.camera.SimpleCamera;
import com.hb.map.navigation.v1.offroute.OffRoute;
import com.hb.map.navigation.v1.offroute.OffRouteDetector;
import com.hb.map.navigation.v1.route.FasterRoute;
import com.hb.map.navigation.v1.route.FasterRouteDetector;
import com.hb.map.navigation.v1.snap.Snap;
import com.hb.map.navigation.v1.snap.SnapToRoute;

class NavigationEngineFactory {

    private OffRoute offRouteEngine;
    private FasterRoute fasterRouteEngine;
    private Snap snapEngine;
    private Camera cameraEngine;

    NavigationEngineFactory() {
        initializeDefaultEngines();
    }

    OffRoute retrieveOffRouteEngine() {
        return offRouteEngine;
    }

    void updateOffRouteEngine(OffRoute offRouteEngine) {
        if (offRouteEngine == null) {
            return;
        }
        this.offRouteEngine = offRouteEngine;
    }

    FasterRoute retrieveFasterRouteEngine() {
        return fasterRouteEngine;
    }

    void updateFasterRouteEngine(FasterRoute fasterRouteEngine) {
        if (fasterRouteEngine == null) {
            return;
        }
        this.fasterRouteEngine = fasterRouteEngine;
    }

    Snap retrieveSnapEngine() {
        return snapEngine;
    }

    void updateSnapEngine(Snap snapEngine) {
        if (snapEngine == null) {
            return;
        }
        this.snapEngine = snapEngine;
    }

    Camera retrieveCameraEngine() {
        return cameraEngine;
    }

    void updateCameraEngine(Camera cameraEngine) {
        if (cameraEngine == null) {
            return;
        }
        this.cameraEngine = cameraEngine;
    }

    private void initializeDefaultEngines() {
        cameraEngine = new SimpleCamera();
        snapEngine = new SnapToRoute();
        offRouteEngine = new OffRouteDetector();
        fasterRouteEngine = new FasterRouteDetector();
    }
}
