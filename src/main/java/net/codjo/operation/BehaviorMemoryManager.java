/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import net.codjo.persistent.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Nettoyeur (alias Victor) de Behavior inutilisé.
 *
 * @author $Author: acharif $
 * @version $Revision: 1.10 $
 */
class BehaviorMemoryManager {
    private OperationSettingsHome home;
    private long refreshDelay;
    private TimerTask task = new MemoryCleanUpTask();
    private Timer timer = new Timer();

    /**
     * Constructeur
     *
     * @param home
     * @param refreshDelay Le delay de dechargement
     */
    BehaviorMemoryManager(OperationSettingsHome home, long refreshDelay) {
        this.home = home;
        this.refreshDelay = refreshDelay;
        timer.schedule(task, 1, refreshDelay);
    }

    /**
     * Nettoyage de la memoire inoccupee depuis un certain temps (voir un temps certain).
     * Si 2 operations pointent sur le meme treatmentBehavior, ces operations ne seront
     * pas etre unloadée.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.10 $
     */
    private class MemoryCleanUpTask extends TimerTask {
        private Map map = new HashMap();

        /**
         * Rempli la map contenant les Operations qui pointent sur le meme
         * treatmentBehavior. Cette map sera utilisee pour ne pas unloader ces
         * operations.
         */
        private void fillSameBehaviorTreatmentMap() {
            map.clear();
            Collection buffer = home.getBuffer();
            for (Iterator iter = buffer.iterator(); iter.hasNext();) {
                OperationSettings os =
                    (OperationSettings)((Reference)iter.next()).getLoadedObject();
                String operationType = os.getOperationType();
                if ("Traitement".equals(operationType)) {
                    if (map.containsKey(os.getBehaviorId())) {
                        List list = ((List)map.get(os.getBehaviorId()));
                        list.add(os);
                    }
                    else {
                        List arrayList = new ArrayList();
                        arrayList.add(os);
                        map.put(os.getBehaviorId(), arrayList);
                    }
                }
            }
        }


        /** @noinspection LocalVariableNamingConvention*/
        public void run() {
            fillSameBehaviorTreatmentMap();
            Collection buffer = home.getBuffer();

            for (Iterator iter = buffer.iterator(); iter.hasNext();) {
                OperationSettings os =
                    (OperationSettings)((Reference)iter.next()).getLoadedObject();


                boolean isBehaviorUsedByAnotherOperation = false;
                if ("Traitement".equals(os.getOperationType())) {
                    isBehaviorUsedByAnotherOperation =
                        (map != null) && (((List)map.get(os.getBehaviorId())).size() > 1);
                }

                boolean unloadable = isUnloadable(os, System.currentTimeMillis());
                if (unloadable && !isBehaviorUsedByAnotherOperation) {
                    os.unloadBehavior();
                }
            }

            for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
                Object operationSettingsList = map.get(iter.next());
                List list = (List)operationSettingsList;
                if (list.size() > 1) {
                    boolean toUnload = true;
                    for (int i = 0; i < list.size(); i++) {
                        OperationSettings o = (OperationSettings)list.get(i);
                        toUnload =
                            toUnload && isUnloadable(o, System.currentTimeMillis());
                    }
                    if (toUnload) {
                        for (int i = 0; i < list.size(); i++) {
                            OperationSettings o = (OperationSettings)list.get(i);
                            o.unloadBehavior();
                        }
                    }
                }
            }

        }


        /**
         * L'operation settings est nettoyable.
         *
         * @param os
         * @param now
         *
         * @return l' OperationSettings peut elle etre nettoyé en memoire
         */
        private boolean isUnloadable(OperationSettings os, long now) {
            boolean behaviorIsNull = os.getLoadedBehavior() != null;
            boolean isLocked = !os.isUnloadBehaviorLocked();
            boolean delay = (os.getTimestamp() + refreshDelay) <= now;
            return os != null && behaviorIsNull && isLocked && delay;
        }
    }
}
