import http from "../http-common";

class GetNode {
    getNode(id) {
        let formData = new FormData();

        formData.append("id", id);

        return http.post("/node", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });
    }

    getId(id){
        let formData = new FormData();

        formData.append("id", id);

        return http.post("/node_id", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });
    }
}

export default new GetNode();
