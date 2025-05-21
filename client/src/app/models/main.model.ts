export interface FortressTable{
  "id":number,
  "name": string,
  "server": string,
  "skills": FortressSkills[],
  "updatedDate": string,
  "clanId": number,
  "clanName": string,
  "level": number,
  "leader": string,
  "playersCount": number,
  "castle": string,
  "reputation": number,
  "alliance": string,
  "coffer": number,
  "holdTime": number
}
export interface FortressSkills{
  "id": number,
  "name": string,
  "effect": string,
  "image": string
}
export interface News{
  "id": number,
  "link": string,
  "title": string,
  "subtitle": string,
  "description": string,
  "createOn": string
}
export interface Events{
  "id": number,
  "title": string,
  "description": string,
  "date": string,
  "server": string,
  "type": string
}
export interface RaidBosses{
  "id": number,
  "name": string,
  "type": string,
  "server": string,
  "date": string,
  "respawnStart": string,
  "respawnEnd": string,
  "countKilling": number,
  "lastKiller": string,
  "lastKillersClan": string,
  "attackersCount": number,
  "createdDate": string,
  "updatedDate": string
}
