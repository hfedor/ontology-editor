import React from "react";
import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";

import UploadFiles from "./components/upload-files.component";
import NodeDiv from "./components/NodeDiv";
import NodesList from "./components/NodesList";
import SearchByID from "./components/SearchByID";

function App() {
  return (
      <>
          <div>
              <SearchByID/>
          </div>
          <div className="container" style={{ width: "600px" }}>
              <div style={{ margin: "20px" }}>
                  <h3>Upload RDF file</h3>
                  <h4>React upload Files</h4>
              </div>

              <UploadFiles/>
          </div>
      </>
  );
}

export default App;