import http from "../http-common";

class SearchService {
    getNodesSearchResult(search) {
        return http.get("/search", { params: { search: search } } );
    }
}

export default new SearchService();