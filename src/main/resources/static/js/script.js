document.addEventListener('DOMContentLoaded', () => {

    //ODM POPUP (ODM)
    const popup = document.getElementById("popup-odm");
    const openBtn = document.getElementById("openPopup");
    const closeBtn = document.getElementById("closePopup");
    const closeIcon = document.getElementById("closePopupIcon");

    if (openBtn && popup) {
        openBtn.addEventListener('click', function () {
            popup.style.display = "flex";
        });
        closeBtn.addEventListener('click', function () {
            popup.style.display = "none";
        });
        closeIcon.addEventListener('click', function () {
            popup.style.display = "none";
        });
    }

    const odmNameInput = document.getElementById("odmName");
    const odmNameError = document.getElementById("odmNameError");

    if (odmNameInput && odmNameError) {
        odmNameInput.setCustomValidity(odmNameError.value);
        odmNameInput.reportValidity();
    }

    const odmEmailInput = document.querySelector("input[type='email'][name='email']");
    const odmEmailError = document.getElementById("odmEmailError");

    if (odmEmailInput && odmEmailError) {
        odmEmailInput.setCustomValidity(odmEmailError.value);
        odmEmailInput.reportValidity();
    }

    //CHIPSET POPUP (Chipset)
    const popupChipset = document.getElementById("popup-chipset");
    const openChipsetBtn = document.getElementById("openChipsetPopup");
    const closeChipsetBtn = document.getElementById("closeChipsetPopup");
    const closeChipsetIcon = document.getElementById("closeChipsetPopupIcon");

    if (openChipsetBtn && popupChipset) {
        openChipsetBtn.addEventListener('click', function () {
            popupChipset.style.display = "flex";
        });
        closeChipsetBtn.addEventListener('click', function () {
            popupChipset.style.display = "none";
        });
        closeChipsetIcon.addEventListener('click', function () {
            popupChipset.style.display = "none";
        });
    }

    const chipsetNameInput = document.getElementById("chipsetName");
    const chipsetNameError = document.getElementById("chipsetNameError");

    if (chipsetNameInput && chipsetNameError) {
        chipsetNameInput.setCustomValidity(chipsetNameError.value);
        chipsetNameInput.reportValidity();
    }


    //ODM / CHIPSET CARD MENU (three dots on each card)
    const dots = document.querySelectorAll('.dots');
    const menus = document.querySelectorAll('.menu-bar');

    function showODM(menu) {
        menu.classList.add('show-menu');
    }

    function hideAllODM() {
        for (let i = 0; i < menus.length; i++) {
            menus[i].classList.remove('show-menu');
        }
    }

    for (let i = 0; i < dots.length; i++) {
        dots[i].addEventListener('click', function (e) {
            e.stopPropagation();
            const menu = this.parentElement.querySelector('.menu-bar');
            hideAllODM();
            hideAllDemo();
            if (menu) {
                showODM(menu);
            }
        });
    }

    for (let i = 0; i < menus.length; i++) {
        menus[i].addEventListener('click', function (e) {
            e.stopPropagation();
        });
    }

    // ODM / CHIPSET RENAME (shared: data-type is "odm" or "chipset")
    document.querySelectorAll('.rename-btn').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            const oldName = this.getAttribute('data-name');
            const type = this.getAttribute('data-type');
            const label = type === 'chipset' ? 'Chipset' : 'ODM';
            const newName = prompt(`Rename ${label} "${oldName}" to:`, oldName);
            if (newName && newName.trim() !== '' && newName.trim() !== oldName) {
                fetch(`/dashboard/${type}/${encodeURIComponent(oldName)}/rename`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `newName=${encodeURIComponent(newName.trim())}`
                })
                    .then(response => {
                        if (response.ok) {
                            window.location.reload();
                        } else {
                            alert(`Failed to rename ${label}. Name may already exist.`);
                        }
                    })
                    .catch(() => alert('Something went wrong while renaming.'));
            }
        });
    });

    // ODM / CHIPSET DELETE (shared: data-type is "odm" or "chipset")
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            const name = this.getAttribute('data-name');
            const type = this.getAttribute('data-type');
            const label = type === 'chipset' ? 'Chipset' : 'ODM';
            if (confirm(`Delete ${label} "${name}"? This cannot be undone.`)) {
                fetch(`/dashboard/${type}/${encodeURIComponent(name)}/delete`, { method: 'POST' })
                    .then(response => {
                        if (response.ok) {
                            window.location.reload();
                        } else {
                            alert(`Failed to delete ${label}. Please try again.`);
                        }
                    })
                    .catch(() => alert('Something went wrong while deleting.'));
            }
        });
    });

    //CREATE PROJECT POPUP (snapdragontech)
    const popupNew = document.getElementById("createNewProject");
    const openBttn = document.getElementById("createProject");
    const closeBttn = document.getElementById("closePopup");
    const _closeIcon = document.getElementById("closePopupIcon");

    if (openBttn && popupNew) {

        openBttn.addEventListener("click", () => {
            popupNew.style.display = "flex";
        });

        closeBttn.addEventListener("click", () => {
            popupNew.style.display = "none";
        });

        _closeIcon.addEventListener("click", () => {
            popupNew.style.display = "none";
        });
    }


    // FILE / FOLDER MENU (three dots on each file card, e.g. Demo SW files)
    const demoDots = document.querySelectorAll('.menu-dots');
    const demoMenus = document.querySelectorAll('.menu-box');

    function showDemo(menu) {
        menu.classList.add('active');
    }

    function hideAllDemo() {
        for (let i = 0; i < demoMenus.length; i++) {
            demoMenus[i].classList.remove('active');
        }
    }

    if (demoDots.length > 0) {
        for (let i = 0; i < demoDots.length; i++) {
            demoDots[i].addEventListener('click', function (e) {
                e.stopPropagation();
                const menu = this.parentElement.querySelector('.menu-box');
                if (menu) {
                    hideAllDemo();
                    hideAllODM();
                    showDemo(menu);
                }
            });
        }

        for (let i = 0; i < demoMenus.length; i++) {
            demoMenus[i].addEventListener('click', function (e) {
                e.stopPropagation();
            });
        }
    }

    // MOVE TO / COPY POPUP (file actions inside a project folder)
    const popupMove = document.getElementById("popup-move");
    const closeMoveBtn = document.getElementById("close_popup");
    const closeMoveIcon = document.getElementById("crossIcon");
    const moveForm = document.getElementById("moveForm");
    const moveTitle = document.getElementById("moveTitle");
    const moveSubmitBtn = document.getElementById("moveSubmitBtn");

    function openMovePopup(fileId, mode) {
        if (!popupMove || !moveForm) return;
        const action = mode === 'copy'
            ? `/dashboard/files/${fileId}/copy`
            : `/dashboard/files/${fileId}/move`;
        moveForm.setAttribute('action', action);
        if (moveTitle) {
            moveTitle.textContent = mode === 'copy' ? 'Copy to' : 'Move to';
        }
        if (moveSubmitBtn) {
            moveSubmitBtn.textContent = mode === 'copy' ? 'Copy here' : 'Move here';
        }
        popupMove.style.display = "flex";
    }

    if (popupMove) {
        closeMoveBtn.addEventListener('click', function () {
            popupMove.style.display = "none";
        });
        closeMoveIcon.addEventListener('click', function () {
            popupMove.style.display = "none";
        });
    }

    document.querySelectorAll('.move-btn').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            openMovePopup(this.getAttribute('data-file-id'), 'move');
        });
    });

    document.querySelectorAll('.copy-btn').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            openMovePopup(this.getAttribute('data-file-id'), 'copy');
        });
    });

    // DELETE FILE
    document.querySelectorAll('.delete-file-btn').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            const fileId = this.getAttribute('data-file-id');
            const fileName = this.getAttribute('data-file-name');
            if (confirm(`Delete "${fileName}"? This cannot be undone.`)) {
                fetch(`/dashboard/files/${fileId}/delete`, { method: 'POST' })
                    .then(response => {
                        if (response.ok) {
                            window.location.reload();
                        } else {
                            alert('Failed to delete file. Please try again.');
                        }
                    })
                    .catch(() => alert('Something went wrong while deleting.'));
            }
        });
    });

    // GLOBAL OUTSIDE CLICK
    document.addEventListener('click', function () {
        hideAllODM();
        hideAllDemo();
    });


    function uploadFile() {
        document.getElementById("uploadForm").submit();
    }


});

window.addEventListener('pageshow', function (event) {
    if (event.persisted) {
        window.location.reload();
    }
});

// DELETE PROJECT (ODM only)
document.querySelectorAll('.delete-project-btn').forEach(btn => {
    btn.addEventListener('click', function (e) {
        e.stopPropagation();
        const name = this.getAttribute('data-odm-name');
        const projectId = this.getAttribute('data-project-id');
        if (confirm('Delete this project? This cannot be undone.')) {
            fetch(`/dashboard/odm/${encodeURIComponent(name)}/projects/${projectId}/delete`, { method: 'POST' })
                .then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Failed to delete project. Please try again.');
                    }
                })
                .catch(() => alert('Something went wrong while deleting.'));
        }
    });
});

// DELETE FOLDER (ODM only)
document.querySelectorAll('.delete-folder-btn').forEach(btn => {
    btn.addEventListener('click', function (e) {
        e.stopPropagation();
        const name = this.getAttribute('data-odm-name');
        const projectId = this.getAttribute('data-project-id');
        const folderId = this.getAttribute('data-folder-id');
        if (confirm('Delete this folder? This cannot be undone.')) {
            fetch(`/dashboard/odm/${encodeURIComponent(name)}/projects/${projectId}/folders/${folderId}/delete`, { method: 'POST' })
                .then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Failed to delete folder. Please try again.');
                    }
                })
                .catch(() => alert('Something went wrong while deleting.'));
        }
    });
});


document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.querySelector('.search-box');
    if (!searchInput) return;

    // Wrap search box in its own fixed-width container (fixes dropdown width issue)
    const wrapper = document.createElement('div');
    wrapper.className = 'header-search-wrapper';
    searchInput.parentElement.insertBefore(wrapper, searchInput);
    wrapper.appendChild(searchInput);

    const resultsBox = document.createElement('div');
    resultsBox.id = 'searchResultsBox';
    resultsBox.style.cssText = `
        position:absolute; top:100%; left:0; right:0;
        background:#fff; border:1px solid #ddd; border-radius:8px;
        box-shadow:0 4px 12px rgba(0,0,0,0.1); z-index:1000;
        max-height:300px; overflow-y:auto; display:none;
    `;
    wrapper.appendChild(resultsBox);

    let debounceTimer;
    let latestData = null;

    searchInput.addEventListener('input', function () {
        clearTimeout(debounceTimer);
        const query = this.value.trim();

        debounceTimer = setTimeout(() => {
            fetch(`/dashboard/search?query=${encodeURIComponent(query)}`)
                .then(res => res.json())
                .then(data => {
                    latestData = data;
                    renderResults(data, query);
                })
                .catch(() => { resultsBox.style.display = 'none'; });
        }, 300);
    });

    // ENTER KEY -> navigate directly
    searchInput.addEventListener('keydown', function (e) {
        if (e.key !== 'Enter') return;
        e.preventDefault();
        const query = this.value.trim();
        if (!query) return;

        fetch(`/dashboard/search?query=${encodeURIComponent(query)}`)
            .then(res => res.json())
            .then(data => {
                if (data.odms.length > 0) {
                    window.location.href = `/dashboard/odm/${encodeURIComponent(data.odms[0].name)}`;
                } else if (data.chipsets.length > 0) {
                    window.location.href = `/dashboard/chipsets/${encodeURIComponent(data.chipsets[0].name)}`;
                } else {
                    alert('No matching ODM or Chipset found.');
                }
            })
            .catch(() => alert('Search failed. Please try again.'));
    });

    function renderResults(data, query) {
        if (!query) {
            resultsBox.style.display = 'none';
            return;
        }

        let html = '';

        if (data.odms.length > 0) {
            html += `<div style="padding:6px 14px; font-weight:600; font-size:12px; color:#999;">ODMs</div>`;
            data.odms.forEach(odm => {
                html += `<a href="/dashboard/odm/${encodeURIComponent(odm.name)}"
                    style="display:block; padding:8px 14px; text-decoration:none; color:#222;">${odm.name}</a>`;
            });
        }

        if (data.chipsets.length > 0) {
            html += `<div style="padding:6px 14px; font-weight:600; font-size:12px; color:#999;">Chipsets</div>`;
            data.chipsets.forEach(chipset => {
                html += `<a href="/dashboard/chipsets/${encodeURIComponent(chipset.name)}"
                    style="display:block; padding:8px 14px; text-decoration:none; color:#222;">${chipset.name}</a>`;
            });
        }

        if (data.odms.length === 0 && data.chipsets.length === 0) {
            html += `<div style="padding:10px 14px; color:#999;">No matches found</div>`;
        }

        resultsBox.innerHTML = html;
        resultsBox.style.display = 'block';
    }

    document.addEventListener('click', (e) => {
        if (!wrapper.contains(e.target)) {
            resultsBox.style.display = 'none';
        }
    });
});
