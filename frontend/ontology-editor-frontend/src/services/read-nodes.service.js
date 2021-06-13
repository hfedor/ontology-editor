import http from "../http-common";

class ReadNodesService {
    getNodesByParentId(node_id) {
        return http.get("/nodes", { params: {node_id: node_id } } );
    }
}

export default new ReadNodesService();