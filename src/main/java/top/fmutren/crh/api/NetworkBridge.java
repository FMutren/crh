package top.fmutren.crh.api;

import top.fmutren.crh.network.ChainKeyStateMessage;
import top.fmutren.crh.network.PipeConnectionMessage;

public interface NetworkBridge {

    void sendPipeConnection(PipeConnectionMessage message);

    void sendChainKeyState(ChainKeyStateMessage message);

}
