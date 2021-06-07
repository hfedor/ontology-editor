import React from "react";
import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";

import UploadFiles from "./components/upload-files.component";


const list = [
    {
        id: 'a',
        firstname: 'Robin',
        lastname: 'Wieruch',
        year: 1988,
        link: "https://pluralsight.com",
    },
    {
        id: 'b',
        firstname: 'Dave',
        lastname: 'Davidds',
        year: 1990,
        link: "https://pluralsight.com",
    },
];

const ComplexList = () => (
    <ul>
        {list.map(item => (
            <li key={item.id}>
                <div>{item.id}</div>
                <div>{item.firstname}</div>
                <div>{item.lastname}</div>
                <div>{item.year}</div>
                <div><a href={item.link}>Visit Pluralsight</a></div>
            </li>
        ))}
    </ul>
);

function App() {
    return (
        <div className="container" style={{ width: "600px" }}>
            <div style={{ margin: "20px" }}>

                <h3>Wczytywanie ontologii z pliku</h3>
                <a href="https://pluralsight.com">Visit Pluralsight</a>
                <ComplexList />
            </div>
            <UploadFiles/>
        </div>
    );
}

export default App;